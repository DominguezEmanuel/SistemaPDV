package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.request.VarianteProductoRequestDTO;
import com.sistemapdv.backend.dto.response.VarianteProductoResponseDTO;
import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.VarianteProducto;
import org.springframework.stereotype.Component;

@Component
public class VarianteProductoMapper {

    public VarianteProducto toVariante(VarianteProductoRequestDTO dto, Producto producto){
        VarianteProducto variante = VarianteProducto.builder()
                .nombre(dto.getNombre())
                .codigoBarras(dto.getCodigoBarras())
                .activo(true)
                .producto(producto)
                .build();
        return variante;
    }

    public VarianteProductoResponseDTO toResponseDTO(VarianteProducto variante){
        return VarianteProductoResponseDTO.builder()
                .idVariante(variante.getIdVariante())
                .nombre(variante.getNombre())
                .codigoBarras(variante.getCodigoBarras())
                .activo(variante.getActivo())
                .idProducto(variante.getProducto().getIdProducto())
                .nombreProducto(variante.getProducto().getNombre())
                .build();
    }
}
