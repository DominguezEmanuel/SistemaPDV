package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.MovimientoRequestDTO;
import com.sistemapdv.backend.dto.response.MovimientoResponseDTO;
import com.sistemapdv.backend.entity.MovimientoStock;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.BusinessException;
import com.sistemapdv.backend.exception.InsufficientStockException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.MovimientoMapper;
import com.sistemapdv.backend.mapper.StockMapper;
import com.sistemapdv.backend.repository.MovimientoRepository;
import com.sistemapdv.backend.repository.StockRepository;
import com.sistemapdv.backend.utils.enums.TipoMovimiento;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class MovimientoService {

    private final StockService stockService;
    private final StockAlertService stockAlertService;
    private final AuthenticationService authenticationService;
    private final MovimientoRepository movimientoRepository;
    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final MovimientoMapper movimientoMapper;

    /**
     * Devuelve un registro de movimiento
     *
     * @param idMovimiento Identificador del movimiento
     * @return Datos del movimiento solicitado
     */
    @Transactional(readOnly = true)
    public MovimientoResponseDTO getRegisterById(Integer idMovimiento){

        MovimientoStock movimiento = movimientoRepository.findById(idMovimiento)
                .orElseThrow( () -> new ResourceNotFoundException("Movimiento con ID "
                + idMovimiento + " no encontrado"));

        return movimientoMapper.toResponseDTO(movimiento);
    }

    /**
     * Devuelve los últimos movimientos de un Stock (los últimos 5)
     *
     * @param idStock Identificador del Stock para consultar sus movimientos
     * @return Listado de los últimos movimientos
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> getLastFiveRecordsByStock(Integer idStock){

        if(!stockRepository.existsById(idStock)){
            throw new ResourceNotFoundException("Registro con ID " + idStock + " no encontrado");
        }

        return movimientoRepository.findTop5ByStockIdStockOrderByFechaHoraDesc(idStock)
                .stream()
                .map(movimientoMapper::toResponseDTO)
                .toList();
    }

    /**
     * Registra un movimiento de Stock en la base de datos
     *
     * @param idStock Identificador del Stock que se va a modificar
     * @param request Solicitud con los datos necesarios para realizar la transacción
     * @return Nuevo registro de movimiento
     */
    @Transactional
    public MovimientoResponseDTO registerMovimiento(Integer idStock, MovimientoRequestDTO request){

        Stock stock = stockRepository.findById(idStock)
                .orElseThrow( () -> new ResourceNotFoundException("Registro con ID " + idStock +
                        " no encontrado"));

        Usuario usuarioAutenticado = authenticationService.getUserAuthenticated();

        validarCantidades(request);

        validarMotivo(request.getTipo(), request.getMotivo());

        int stockAnterior = stock.getCantidadDisponible();

        int cantidadMovimiento = calcularCantidad(request, stockAnterior);

        int stockResultante = stockAnterior + cantidadMovimiento;

        stock.setCantidadDisponible(stockResultante);

        // Verificar si el registro de Stock cambió de estado a 'STOCK_BAJO' o 'SIN_STOCK'
        // para enviar alerta al propietario
        if(!stockService.tieneMismoEstado(stock)){
            stock.setEstado(stockService.obtenerEstadoStock(stock.getCantidadDisponible(),
                    stock.getStockMinimo()));
            stockAlertService.procesarCambioEstado(stockMapper.toStockAlertDTO(stock));
        }

        MovimientoStock nuevoMovimiento = movimientoMapper.toMovimiento(request, usuarioAutenticado,
                stock, stockAnterior, cantidadMovimiento);

        movimientoRepository.save(nuevoMovimiento);

        return movimientoMapper.toResponseDTO(nuevoMovimiento);
    }

    /**
     * Verifica que 'motivo' se encuentre dentro de la request
     *
     * Para 'SALIDA' y 'AJUSTE': 'motivo' es obligatorio
     *
     * Para 'ENTRADA': 'motivo' es opcional
     *
     * @param tipo Tipo de movimiento de la request
     * @param motivo Motivo por el cual se realiza la transacción
     */
    private void validarMotivo(TipoMovimiento tipo, String motivo){
        if(tipo.equals(TipoMovimiento.SALIDA) || tipo.equals(TipoMovimiento.AJUSTE)){
            if(motivo == null || motivo.isEmpty()){
                throw new IllegalArgumentException("El motivo es obligatorio para el tipo de movimiento '"
                        + tipo + "'");
            }
        }
    }

    /**
     * Verifica que las cantidades enviadas sean válidas
     *
     * Para 'ENTRADA' o 'SALIDA'-> se debe enviar 'cantidad' en la request y debe ser > 0
     *
     * Para 'AJUSTE'-> se debe enviar 'stockFisico' en la request y debe ser >= 0
     *
     * @param request Solicitud con los datos necesarios
     */
    private void validarCantidades(MovimientoRequestDTO request){
        if(request.getTipo().equals(TipoMovimiento.ENTRADA)
                || request.getTipo().equals(TipoMovimiento.SALIDA)){
            if(request.getCantidad() == null || request.getCantidad() <= 0)
                throw new IllegalArgumentException("La cantidad enviada es inválida");
        }else{
            if(request.getStockFisico() == null || request.getStockFisico() < 0)
                throw new IllegalArgumentException("El stock enviado es inválido");
        }
    }

    /**
     * Calcula la cantidad de unidades que va a modificar del registro de Stock
     *
     * @param request Solicitud de dónde se obtiene el 'tipoMovimiento' y la cantidad para modificar
     * @param stockAnterior Cantidad disponible del Stock antes de realizar la solicitud
     * @return Cantidad que debe modificar del registro de Stock
     */
    private int calcularCantidad(MovimientoRequestDTO request, int stockAnterior){
        int cantidad = 0;
        switch (request.getTipo()){
            case ENTRADA:
                cantidad = request.getCantidad();
                break;

            case SALIDA:
                cantidad = -request.getCantidad();
                if(stockAnterior + cantidad < 0){
                    throw new InsufficientStockException("El stock es insuficiente");
                }
                break;

            case AJUSTE:
                cantidad = request.getStockFisico() - stockAnterior;
                if(cantidad == 0){
                    throw new BusinessException("El stock físico coincide con el stock actual");
                }
                break;

            default:
                throw new BusinessException("Tipo de movimiento inválido");
        }
        return cantidad;
    }
}
