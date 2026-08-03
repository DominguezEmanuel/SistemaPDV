package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.CategoriaDTO;
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

    public Producto toProducto(ProductoRequestDTO dto, Categoria categoria){

        Producto producto = Producto.builder()
                .nombre(dto.getNombre().trim())
                .imagen(dto.getImagen().trim())
                .precioMinorista(dto.getPrecioMinorista())
                .precioMayorista(dto.getPrecioMayorista())
                .minimoMayorista(dto.getMinimoMayorista())
                .activo(true)
                .categoria(categoria)
                .build();

        return producto;
    }

    public ProductoResponseDTO toResponseDTO(Producto producto){
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setImagen(producto.getImagen());
        dto.setPrecioMinorista(producto.getPrecioMinorista());
        dto.setPrecioMayorista(producto.getPrecioMayorista());
        dto.setMinimoMayorista(producto.getMinimoMayorista());
        dto.setActivo(producto.getActivo());
        CategoriaDTO categoria = categoriaMapper.toCategoriaDTO(producto.getCategoria());
        dto.setCategoria(categoria);
        return dto;
    }
}
