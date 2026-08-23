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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final VarianteProductoRepository varianteRepository;
    private final CanalVentaRepository canalVentaRepository;
    private final StockMapper stockMapper;

    public StockService(StockRepository stockRepository, VarianteProductoRepository varianteRepository,
                        CanalVentaRepository canalVentaRepository, StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.varianteRepository = varianteRepository;
        this.canalVentaRepository = canalVentaRepository;
        this.stockMapper = stockMapper;
    }

    @Transactional(readOnly = true)
    public StockResponseDTO getStockById(Integer id){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Stock con ID "
                + id + " no encontrado"));
        return stockMapper.toResponseDTO(stock);
    }

    @Transactional(readOnly = true)
    public List<StockResponseDTO> getAllStocks(){
        return stockRepository.findAll()
                .stream()
                .map(stockMapper::toResponseDTO)
                .toList();
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
                        "variante " + variante.getNombre() + " y canal " + canal.getNombre()));

        return stockMapper.toResponseDTO(stock);
    }

    @Transactional
    public StockResponseDTO createStock(StockRequestDTO request){

        VarianteProducto variante = varianteRepository.findById(request.getIdVariante())
                .orElseThrow(() -> new ResourceNotFoundException("Variante con ID "
                        + request.getIdVariante() + " no encontrada"));

        CanalVenta canal = canalVentaRepository.findById(request.getIdCanalVenta())
                .orElseThrow(() -> new ResourceNotFoundException("Canal con ID "
                        + request.getIdCanalVenta() + " no encontrado"));

        /*if(request.getCantidadDisponible() < 1)
            throw new IllegalArgumentException("La cantidad mínima disponible debe ser mayor o igual 1");

        if(request.getStockMinimo() < 3)
            throw new IllegalArgumentException("El stock mínimo no puede ser menor a 3");*/

        // Validar que no exista la combinación Variante + Canal
        if(stockRepository.existsByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
                request.getIdVariante(), request.getIdCanalVenta()))
            throw new ResourceDuplicatedException("Ya existe un stock para esta combinacion " +
                    "de variante y canal de venta");

        Stock nuevoStock = stockMapper.toStock(request, variante, canal);

        stockRepository.save(nuevoStock);

        return stockMapper.toResponseDTO(nuevoStock);
    }

    /* De esto se encargará MovimientoStock
    @Transactional
    public StockResponseDTO editCantidadDisponible(Integer idStock, Integer nuevaCantidad){
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(()-> new ResourceNotFoundException("Stock con ID " +
                        idStock + " no encontrado"));

        if(nuevaCantidad < 0)
            throw new IllegalArgumentException("La cantidad disponible no puede ser menor a 0");

        stock.setCantidadDisponible(nuevaCantidad);

        return stockMapper.toResponseDTO(stock);
    }*/

    @Transactional
    public StockResponseDTO editStockMinimo(Integer idStock, Integer nuevoStockMinimo){
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(()-> new ResourceNotFoundException("Registro de stock con ID " +
                        idStock + " no encontrado"));

        if(nuevoStockMinimo < 3)
            throw new IllegalArgumentException("El stock mínimo no puede ser menor a 3");

        stock.setStockMinimo(nuevoStockMinimo);

        return stockMapper.toResponseDTO(stock);
    }
}
