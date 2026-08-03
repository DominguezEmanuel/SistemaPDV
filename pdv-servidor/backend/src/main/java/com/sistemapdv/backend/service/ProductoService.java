package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.ProductoRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.entity.Categoria;
import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.ProductoMapper;
import com.sistemapdv.backend.repository.CategoriaRepository;
import com.sistemapdv.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
    }

    public ProductoResponseDTO findById(Integer id){
        Producto producto = productoRepository.findByIdWithCategoria(id)
                .orElseThrow( ()->
                        new ResourceNotFoundException("Producto con id " + id + " no encontrado"));
        return productoMapper.toResponseDTO(producto);
    }

    public ProductoResponseDTO findByName(String nombre){
        Producto producto = productoRepository.findByNombreWithCategoria(nombre)
                .orElseThrow(()->
                        new ResourceNotFoundException("Producto con nombre " + nombre + " no encontrado"));
        return productoMapper.toResponseDTO(producto);
    }

    public List<ProductoResponseDTO> findAllProducts(){
        List<Producto> productos = productoRepository.findAllWithCategoria();
        return productos.stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    public ProductoResponseDTO createProduct(ProductoRequestDTO request){
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Categoria con ID " + request.getIdCategoria()
                        + " no encontrada"
                ));
        // Asegurar que el precio mayorista sea menor al minorista
        if(request.getPrecioMayorista().compareTo(request.getPrecioMinorista()) > 0)
            throw new IllegalArgumentException("El precio mayorista no puede ser mayor " +
                    "al precio minorista");

        if(productoRepository.existsByNombreIgnoreCase(request.getNombre()))
            throw new IllegalArgumentException("Ya existe un producto con el nombre ingresado");

        Producto producto = productoMapper.toProducto(request, categoria);

        Producto productoGuardado = productoRepository.save(producto);

        return productoMapper.toResponseDTO(productoGuardado);
    }
}
