package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.response.ProductoCanalResponseDTO;
import com.sistemapdv.backend.entity.ProductoCanal;
import org.springframework.stereotype.Component;

@Component
public class ProductoCanalMapper {

    public ProductoCanalResponseDTO toResponseDTO(ProductoCanal entity){

        return ProductoCanalResponseDTO.builder()
                .idProductoCanal(entity.getIdProductoCanal())
                .idProducto(entity.getProducto().getIdProducto())
                .nombreProducto(entity.getProducto().getNombre())
                .idCanalVenta(entity.getCanalVenta().getIdCanalVenta())
                .nombreCanalVenta(entity.getCanalVenta().getNombre())
                .limiteMayorista(entity.getLimiteMayorista())
                .build();
    }
}
