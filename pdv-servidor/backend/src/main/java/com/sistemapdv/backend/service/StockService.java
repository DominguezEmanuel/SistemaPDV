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

@Service
public class StockService {

    private final StockRepository stockRepository;

    private final VarianteProductoRepository varianteRepository;

    private final CanalVentaRepository canalVentaRepository;

    private final StockMapper stockMapper;

    public StockService(StockRepository stockRepository, VarianteProductoRepository varianteRepository, CanalVentaRepository canalVentaRepository, StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.varianteRepository = varianteRepository;
        this.canalVentaRepository = canalVentaRepository;
        this.stockMapper = stockMapper;
    }

    @Transactional
    public StockResponseDTO addStock(StockRequestDTO request){
        if(stockRepository.existsByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
                request.getIdVariante(), request.getIdCanalVenta()))
            throw new ResourceDuplicatedException("Ya existe un stock para esa variante y canal de venta");

        VarianteProducto variante = varianteRepository.findById(request.getIdVariante())
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

        CanalVenta canal = canalVentaRepository.findById(request.getIdCanalVenta())
                .orElseThrow(() -> new ResourceNotFoundException("Canal no encontrado"));

        Stock nuevoStock = Stock.builder()
                .cantidadDisponible(request.getCantidadDisponible())
                .stockMinimo(request.getStockMinimo())
                .varianteProducto(variante)
                .canalVenta(canal)
                .build();

        stockRepository.save(nuevoStock);

        return stockMapper.toResponseDTO(nuevoStock);
    }
}
