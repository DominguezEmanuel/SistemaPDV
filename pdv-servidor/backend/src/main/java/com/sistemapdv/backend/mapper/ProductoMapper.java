package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.request.ProductoRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.entity.Categoria;
import com.sistemapdv.backend.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    private final CategoriaMapper categoriaMapper;

    public ProductoMapper(CategoriaMapper categoriaMapper) {
        this.categoriaMapper = categoriaMapper;
    }

    public Producto toProducto(ProductoRequestDTO dto,
                               Categoria categoria,
                               String imagen){
        Producto producto = Producto.builder()
                .nombre(dto.getNombre().trim())
                .imagen(imagen)
                .precioMinorista(dto.getPrecioMinorista())
                .precioMayorista(dto.getPrecioMayorista())
                .minimoMayorista(dto.getMinimoMayorista())
                .activo(true)
                .tieneVariantes(dto.getTieneVariantes())
                .categoria(categoria)
                .build();

        return producto;
    }

    public ProductoResponseDTO toResponseDTO(Producto producto){
        ProductoResponseDTO dto = ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .imagen(producto.getImagen())
                .precioMinorista(producto.getPrecioMinorista())
                .precioMayorista(producto.getPrecioMayorista())
                .minimoMayorista(producto.getMinimoMayorista())
                .activo(producto.getActivo())
                .tieneVariantes(producto.getTieneVariantes())
                .categoria(categoriaMapper.toCategoriaDTO(producto.getCategoria()))
                .build();
        return dto;
    }
}
