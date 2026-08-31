package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.StockRequestDTO;
import com.sistemapdv.backend.dto.response.StockResponseDTO;
import com.sistemapdv.backend.entity.CanalVenta;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.entity.VarianteProducto;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.StockMapper;
import com.sistemapdv.backend.repository.CanalVentaRepository;
import com.sistemapdv.backend.repository.StockRepository;
import com.sistemapdv.backend.repository.VarianteProductoRepository;
import com.sistemapdv.backend.repository.specification.StockSpecification;
import com.sistemapdv.backend.utils.enums.EstadoStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private static final Integer STOCK_MINIMO_ACEPTADO = 3;

    private final StockRepository stockRepository;
    private final VarianteProductoRepository varianteRepository;
    private final CanalVentaRepository canalVentaRepository;
    private final StockMapper stockMapper;

    private final StockAlertService stockAlertService;

    public StockService(StockRepository stockRepository, VarianteProductoRepository varianteRepository,
                        CanalVentaRepository canalVentaRepository, StockMapper stockMapper, StockAlertService stockAlertService) {
        this.stockRepository = stockRepository;
        this.varianteRepository = varianteRepository;
        this.canalVentaRepository = canalVentaRepository;
        this.stockMapper = stockMapper;
        this.stockAlertService = stockAlertService;
    }

    @Transactional(readOnly = true)
    public StockResponseDTO getStockById(Integer id){

        Stock stock = stockRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro con ID "
                + id + " no encontrado"));

        return stockMapper.toResponseDTO(stock);
    }

    @Transactional(readOnly = true)
    public Page<StockResponseDTO> getAllStocks(Pageable pageable){

        Page<Stock> stocks = stockRepository.findAll(pageable);

        return stocks.map(stockMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<StockResponseDTO> getStocksByIdVariant(Integer idVariante){

        if(!varianteRepository.existsById(idVariante))
            throw new ResourceNotFoundException("Variante con ID " +
                idVariante + " no encontrada");

        return stockRepository.findByVarianteProductoIdVariante(idVariante)
                .stream()
                .map(stockMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StockResponseDTO> getStocksByIdChannel(Integer idCanal){

        if(!canalVentaRepository.existsById(idCanal))
            throw new ResourceNotFoundException("Canal con ID " +
                    idCanal + " no encontrado");

        return stockRepository.findByCanalVentaIdCanalVenta(idCanal)
                .stream()
                .map(stockMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockResponseDTO getStockByChannelAndVariant(Integer idCanal, Integer idVariante) {

        // Verificar que exista la Variante
        VarianteProducto variante = varianteRepository.findById(idVariante)
                .orElseThrow(()-> new ResourceNotFoundException("Canal con ID " +
                    idCanal + " no encontrado"));

        // Verificar que exista la Canal
        CanalVenta canal = canalVentaRepository.findById(idCanal)
                .orElseThrow(()-> new ResourceNotFoundException("Variante con ID " +
                    idVariante + " no encontrada"));

        Stock stock = stockRepository.findByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
                        idVariante, idCanal)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado para " +
                        "variante '" + variante.getNombre() + "' y canal '" + canal.getNombre() + "'"));

        return stockMapper.toResponseDTO(stock);
    }

    @Transactional
    public StockResponseDTO createStock(StockRequestDTO request){

        VarianteProducto variante = varianteRepository.findById(request.getIdVariante())
                .orElseThrow(() -> new ResourceNotFoundException("Variante con ID "
                        + request.getIdVariante() + " no encontrada"));

        if(!variante.getActivo()){
            throw new IllegalArgumentException("La variante seleccionada se encuentra inactiva");
        }

        CanalVenta canal = canalVentaRepository.findById(request.getIdCanalVenta())
                .orElseThrow(() -> new ResourceNotFoundException("Canal con ID "
                        + request.getIdCanalVenta() + " no encontrado"));

        // Validar que no exista la combinación Variante + Canal
        if(stockRepository.existsByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
                request.getIdVariante(), request.getIdCanalVenta()))
            throw new ResourceDuplicatedException("Ya existe un registro para esta combinacion " +
                    "de variante y canal de venta");

        Stock nuevoStock = stockMapper.toStock(request, variante, canal);

        // Obtener el estado del stock
        nuevoStock.setEstado(obtenerEstadoStock(nuevoStock.getCantidadDisponible(), nuevoStock.getStockMinimo()));

        stockRepository.save(nuevoStock);

        return stockMapper.toResponseDTO(nuevoStock);
    }

    private EstadoStock obtenerEstadoStock(Integer cantidad, Integer minimo){

        if(cantidad == 0) {
            return EstadoStock.SIN_STOCK;
        }

        if(cantidad <= minimo){
            return EstadoStock.STOCK_BAJO;
        }

        return EstadoStock.DISPONIBLE;
    }

    /* De esto se encargará MovimientoStock */
    @Transactional
    public StockResponseDTO editStock(Integer idStock, Integer nuevaCantidad){
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(()-> new ResourceNotFoundException("Registro con ID " +
                        idStock + " no encontrado"));

        if(nuevaCantidad < 0)
            throw new IllegalArgumentException("La cantidad disponible no puede ser menor a 0");

        stock.setCantidadDisponible(nuevaCantidad);
        EstadoStock estadoAnterior = stock.getEstado();

        if(!mismoEstado(stock)){
            if(stock.getCantidadDisponible() == 0){
                stock.setEstado(EstadoStock.SIN_STOCK);
            }else{
                stock.setEstado(EstadoStock.STOCK_BAJO);
            }
            stockAlertService.procesarCambioEstado(stock, estadoAnterior);
        }

        return stockMapper.toResponseDTO(stock);
    }

    private Boolean mismoEstado(Stock stock){
        EstadoStock estadoOriginal = stock.getEstado();

        EstadoStock nuevoEstado = obtenerEstadoStock(stock.getCantidadDisponible(), stock.getStockMinimo());

        return estadoOriginal.equals(nuevoEstado);
    }

    @Transactional
    public StockResponseDTO editStockMinimo(Integer idStock, Integer nuevoStockMinimo){
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(()-> new ResourceNotFoundException("Registro con ID " +
                        idStock + " no encontrado"));

        if(nuevoStockMinimo < STOCK_MINIMO_ACEPTADO)
            throw new IllegalArgumentException("El stock mínimo no puede ser menor a 3");

        stock.setStockMinimo(nuevoStockMinimo);

        return stockMapper.toResponseDTO(stock);
    }

    @Transactional(readOnly = true)
    public Page<StockResponseDTO> getByFilters(String nombre, Integer idCanal, EstadoStock estado, Pageable pageable){
        Specification<Stock> specification = (root, query, cb) -> null;

        if(nombre != null && !nombre.isBlank()){
            specification = specification.and(StockSpecification.buscarPorTexto(nombre));
        }

        if(idCanal != null){
            specification = specification.and(StockSpecification.porCanal(idCanal));
        }

        if(estado != null){
            specification = specification.and(StockSpecification.porEstado(estado));
        }

        return stockRepository
                .findAll(specification, pageable)
                .map(stockMapper::toResponseDTO);
    }
}
