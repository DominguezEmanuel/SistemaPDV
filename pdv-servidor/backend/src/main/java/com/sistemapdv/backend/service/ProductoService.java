package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.ProductoRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.dto.response.VarianteProductoResponseDTO;
import com.sistemapdv.backend.entity.Categoria;
import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.VarianteProducto;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.ProductoMapper;
import com.sistemapdv.backend.mapper.VarianteProductoMapper;
import com.sistemapdv.backend.repository.CategoriaRepository;
import com.sistemapdv.backend.repository.ProductoRepository;
import com.sistemapdv.backend.repository.VarianteProductoRepository;
import com.sistemapdv.backend.repository.specification.ProductoSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final CloudinaryService cloudinaryService;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final VarianteProductoRepository varianteRepository;
    private final ProductoMapper productoMapper;
    private final VarianteProductoMapper varianteMapper;

    public ProductoService(CloudinaryService cloudinaryService, ProductoRepository productoRepository, CategoriaRepository categoriaRepository, VarianteProductoRepository varianteRepository, ProductoMapper productoMapper, VarianteProductoMapper varianteMapper) {
        this.cloudinaryService = cloudinaryService;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.varianteRepository = varianteRepository;
        this.productoMapper = productoMapper;
        this.varianteMapper = varianteMapper;
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO findById(Integer id){
        Producto producto = productoRepository.findByIdWithCategoria(id)
                .orElseThrow( ()->
                        new ResourceNotFoundException("Producto con id " + id + " no encontrado"));
        return productoMapper.toResponseDTO(producto);
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO findByName(String nombre){
        Producto producto = productoRepository.findByNombreWithCategoria(nombre)
                .orElseThrow(()->
                        new ResourceNotFoundException("Producto con nombre " + nombre + " no encontrado"));
        return productoMapper.toResponseDTO(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filterByFilters(String nombre,
                                                    Integer idCategoria,
                                                     Boolean activo){
        Specification<Producto> specification =
                (root, query, criteriaBuilder) -> null;

        if (nombre != null && !nombre.isBlank()) {
            specification = specification.and(
                    ProductoSpecification.nombreContiene(nombre.trim())
            );
        }

        if (idCategoria != null) {
            specification = specification.and(
                    ProductoSpecification.perteneceACategoria(idCategoria)
            );
        }

        if (activo != null) {
            specification = specification.and(
                    ProductoSpecification.tieneEstado(activo)
            );
        }

        return productoRepository
                .findAll(specification)
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> findAllProducts(){
        List<Producto> productos = productoRepository.findAllWithCategoria();
        return productos
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VarianteProductoResponseDTO> getVariantsByProduct(Integer idProducto){
        // Verificar que el ID de Producto existe
        if(!productoRepository.existsById(idProducto))
            throw new ResourceNotFoundException("Producto con id " + idProducto + " no encontrado");
        // Buscar las variantes del producto
        List<VarianteProducto> variantes = varianteRepository.findByProductoId(idProducto);

        return variantes
                .stream()
                .map(varianteMapper::toResponseDTO)
                .toList();
    }

    @Transactional
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

        // Sube imagen a Cloudinary y retorna la URL
        String imagenUrl = cloudinaryService.uploadImage(request.getImagen());

        Producto nuevoProducto = productoMapper.toProducto(request, categoria, imagenUrl);

        productoRepository.save(nuevoProducto);

        return productoMapper.toResponseDTO(nuevoProducto);
    }

    @Transactional
    public ProductoResponseDTO updateImage(Integer idProducto, MultipartFile imagen){
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto con id " + idProducto + " no encontrado"
                        ));
        String imagenUrl = cloudinaryService.uploadImage(imagen);

        producto.setImagen(imagenUrl);

        return productoMapper.toResponseDTO(producto);
    }

    @Transactional
    public ProductoResponseDTO changeStatusProduct(Integer idProducto, boolean activo){
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto con id " + idProducto + " no encontrado"
                        ));

        if(producto.getActivo().equals(activo)){
            throw new IllegalArgumentException(
                    "El producto ya se encuentra "
                            + (activo ? "habilitado" : "deshabilitado"));
        }

        producto.setActivo(activo);

        return productoMapper.toResponseDTO(producto);
    }


    @Transactional
    public ProductoResponseDTO updateProduct(Integer id,
                                             ProductoRequestDTO request){
        // Buscar el producto
        Producto producto = productoRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Producto con id " + id + " no encontrado"));

        // Buscar la categoría
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(()-> new ResourceNotFoundException("Categoria con ID "
                        + request.getIdCategoria() + " no encontrada"));

        // Validar que NO exista otro producto con el mismo nombre
        Optional<Producto> productoExistente =
                productoRepository.findByNombreIgnoreCase(request.getNombre());
        if(productoExistente.isPresent() &&
            !productoExistente.get().getIdProducto().equals(id)){
            throw new ResourceDuplicatedException("Ya existe un producto con ese nombre");
        }

        // Validar precios
        if (request.getPrecioMayorista()
                .compareTo(request.getPrecioMinorista()) > 0) {
            throw new IllegalArgumentException("El precio mayorista no puede ser mayor al precio minorista.");
        }

        // Actualizar datos
        producto.setNombre(request.getNombre().trim());
        String imagenUrl = cloudinaryService.uploadImage(request.getImagen());
        producto.setImagen(imagenUrl);
        producto.setPrecioMinorista(request.getPrecioMinorista());
        producto.setPrecioMayorista(request.getPrecioMayorista());
        producto.setMinimoMayorista(request.getMinimoMayorista());
        producto.setCategoria(categoria);

        return productoMapper.toResponseDTO(producto);
    }
}
