package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.response.StockResponseDTO;
import com.sistemapdv.backend.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public StockResponseDTO toResponseDTO(Stock stock){
        return StockResponseDTO.builder()
                .idStock(stock.getIdStock())
                .cantidadDisponible(stock.getCantidadDisponible())
                .stockMinimo(stock.getStockMinimo())
                .idVariante(stock.getVarianteProducto().getIdVariante())
                .nombreVariante(stock.getVarianteProducto().getNombre())
                .idCanalVenta(stock.getCanalVenta().getIdCanalVenta())
                .canalVenta(stock.getCanalVenta().getNombre())
                .build();
    }
}
