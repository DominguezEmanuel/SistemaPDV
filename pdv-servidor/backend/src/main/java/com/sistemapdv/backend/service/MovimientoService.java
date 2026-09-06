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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoService {

    private final StockService stockService;
    private final StockAlertService stockAlertService;
    private final AuthenticationService authenticationService;
    private final MovimientoRepository movimientoRepository;
    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final MovimientoMapper movimientoMapper;

    public MovimientoService(StockService stockService, StockAlertService stockAlertService, AuthenticationService authenticationService, MovimientoRepository movimientoRepository, StockRepository stockRepository, StockMapper stockMapper, MovimientoMapper movimientoMapper) {
        this.stockService = stockService;
        this.stockAlertService = stockAlertService;
        this.authenticationService = authenticationService;
        this.movimientoRepository = movimientoRepository;
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.movimientoMapper = movimientoMapper;
    }

    @Transactional
    public MovimientoResponseDTO registerMovimiento(Integer idStock, MovimientoRequestDTO request){

        Stock stock = stockRepository.findById(idStock)
                .orElseThrow( () -> new ResourceNotFoundException("Registro con ID " + idStock +
                        " no encontrado"));

        Integer stockAnterior = stock.getCantidadDisponible();

        Integer cantidadMovimiento = calcularCantidad(request, stockAnterior);

        Integer stockResultante = stockAnterior + cantidadMovimiento;

        stock.setCantidadDisponible(stockResultante);

        if(!stockService.tieneMismoEstado(stock)){
            stock.setEstado(stockService.obtenerEstadoStock(stock.getCantidadDisponible(),
                    stock.getStockMinimo()));
            stockAlertService.procesarCambioEstado(stockMapper.toStockAlertDTO(stock));
        }

        Usuario usuarioAutenticado = authenticationService.getUserAuthenticated();

        MovimientoStock movimiento = movimientoMapper.toMovimiento(request, usuarioAutenticado,
                stock, stockAnterior, cantidadMovimiento);

        movimientoRepository.save(movimiento);

        return movimientoMapper.toResponseDTO(movimiento);
    }

    private Integer calcularCantidad(MovimientoRequestDTO request, Integer stockAnterior){
        Integer cantidad = 0;
        switch (request.getTipo()){
            case ENTRADA:
                cantidad = request.getCantidad();
                break;

            case SALIDA:
                cantidad = -request.getCantidad();
                if(stockAnterior + cantidad < 0){
                    throw new InsufficientStockException("Stock insuficiente");
                }
                break;

            case AJUSTE:
                cantidad = request.getStockFisico() - stockAnterior;
                if(cantidad == 0){
                    throw new BusinessException("El stock físico coincide con el stock actual");
                }
                break;

            default:
                throw new BusinessException("Tipo de movimiento no válido");
        }
        return cantidad;
    }
}
