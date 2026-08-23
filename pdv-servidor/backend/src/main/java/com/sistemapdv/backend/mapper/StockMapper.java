package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.request.StockRequestDTO;
import com.sistemapdv.backend.dto.response.StockResponseDTO;
import com.sistemapdv.backend.entity.CanalVenta;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.entity.VarianteProducto;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public StockResponseDTO toResponseDTO(Stock stock){
        return StockResponseDTO.builder()
                .idStock(stock.getIdStock())
                .cantidadDisponible(stock.getCantidadDisponible())
                .stockMinimo(stock.getStockMinimo())
                .idProducto(stock.getVarianteProducto().getProducto().getIdProducto())
                .nombreProducto(stock.getVarianteProducto().getProducto().getNombre())
                .idVariante(stock.getVarianteProducto().getIdVariante())
                .nombreVariante(stock.getVarianteProducto().getNombre())
                .codigoBarras(stock.getVarianteProducto().getCodigoBarras())
                .codigoInterno(stock.getVarianteProducto().getCodigoInterno())
                .idCanalVenta(stock.getCanalVenta().getIdCanalVenta())
                .nombreCanalVenta(stock.getCanalVenta().getNombre())
                .build();
    }

    public Stock toStock(StockRequestDTO request, VarianteProducto variante, CanalVenta canal){
        return Stock.builder()
                .cantidadDisponible(request.getCantidadDisponible())
                .stockMinimo(request.getStockMinimo())
                .varianteProducto(variante)
                .canalVenta(canal)
                .build();
    }
}
