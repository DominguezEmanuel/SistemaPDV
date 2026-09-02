package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.ProductoRequestDTO;
import com.sistemapdv.backend.dto.request.VarianteProductoRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoCanalResponseDTO;
import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.dto.response.StockProductoResponseDTO;
import com.sistemapdv.backend.dto.response.VarianteProductoResponseDTO;
import com.sistemapdv.backend.entity.*;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.ProductoCanalMapper;
import com.sistemapdv.backend.mapper.ProductoMapper;
import com.sistemapdv.backend.mapper.VarianteProductoMapper;
import com.sistemapdv.backend.repository.*;
import com.sistemapdv.backend.repository.specification.ProductoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final VarianteProductoService varianteService;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final StockRepository stockRepository;
    private final VarianteProductoRepository varianteRepository;
    private final ProductoCanalRepository productoCanalRepository;
    private final ProductoMapper productoMapper;
    private final VarianteProductoMapper varianteMapper;
    private final ProductoCanalMapper productoCanalMapper;

    public ProductoService(CloudinaryService cloudinaryService, VarianteProductoService varianteService, ProductoRepository productoRepository, CategoriaRepository categoriaRepository, StockRepository stockRepository, VarianteProductoRepository varianteRepository, ProductoCanalRepository productoCanalRepository, ProductoMapper productoMapper, VarianteProductoMapper varianteMapper, ProductoCanalMapper productoCanalMapper) {
        this.cloudinaryService = cloudinaryService;
        this.varianteService = varianteService;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.stockRepository = stockRepository;
        this.varianteRepository = varianteRepository;
        this.productoCanalRepository = productoCanalRepository;
        this.productoMapper = productoMapper;
        this.varianteMapper = varianteMapper;
        this.productoCanalMapper = productoCanalMapper;
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO findById(Integer id){
        Producto producto = productoRepository.findByIdWithCategoria(id)
                .orElseThrow( ()->
                        new ResourceNotFoundException("Producto con ID " + id + " no encontrado"));
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
    public Page<ProductoResponseDTO> filterByFilters(String nombre,
                                                     Integer idCategoria,
                                                     Boolean activo,
                                                     Pageable pageable){
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
                .findAll(specification, pageable)
                .map(productoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filterByName(String nombre){
        Specification<Producto> specification = (root, query, cb) -> null;

        // Filtra por nombre y 'activo' == true
        if(nombre != null && !nombre.isBlank()){
            specification = specification.and(
                    ProductoSpecification.contieneNombre(nombre.trim())
            );
            specification = specification.and(
                    ProductoSpecification.tieneEstado(true)
            );
        }

        return productoRepository
                .findAll(specification)
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StockProductoResponseDTO> getStockByProductId(Integer idProducto){
        if(!productoRepository.existsById(idProducto))
            throw new ResourceNotFoundException("El producto con ID " +
                    idProducto + " no existe");

        List<Stock> stocksProducto = stockRepository.findByProductoId(idProducto);

        return stocksProducto
                .stream()
                .map(productoMapper::toStockProductoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> findAllProducts(Pageable pageable){
        Page<Producto> productos = productoRepository.findAll(pageable);
        return productos.map(productoMapper::toResponseDTO);
    }

    // Mejorar
    @Transactional(readOnly = true)
    public List<VarianteProductoResponseDTO> getVariantsByProduct(Integer idProducto){
        // Verificar que el ID de Producto existe
        if(!productoRepository.existsById(idProducto))
            throw new ResourceNotFoundException("Producto con ID " + idProducto + " no encontrado");
        // Buscar las variantes del producto
        List<VarianteProducto> variantes = varianteRepository.findByProductoId(idProducto);

        return variantes
                .stream()
                .map(varianteMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProductoResponseDTO createProduct(ProductoRequestDTO request){
        // Validar Categoria existente
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

        // Para la creación, la imagen es obligatoria
        if(request.getImagen() == null || request.getImagen().isEmpty()) {
            throw new IllegalArgumentException("La imagen es obligatoria para crear el producto");
        }

        if (Boolean.TRUE.equals(request.getTieneVariantes())
                && !request.getCodigoBarras().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código de barras debe registrarse individualmente en cada variante"
            );
        }

        String imagenUrl = cloudinaryService.uploadImage(request.getImagen());

        Producto nuevoProducto = productoMapper.toProducto(request, categoria, imagenUrl);

        productoRepository.save(nuevoProducto);

        // Crear variante 'UNICA'
        if(Boolean.FALSE.equals(nuevoProducto.getTieneVariantes())){
            crearVarianteUnica(nuevoProducto, request.getCodigoBarras());
        }

        return productoMapper.toResponseDTO(nuevoProducto);
    }

    private void crearVarianteUnica(Producto producto, String codigoBarras){
        VarianteProducto varianteUnica = VarianteProducto.builder()
                .nombre("Unica")
                .codigoBarras(codigoBarras.isEmpty() || codigoBarras == null ? null : codigoBarras)
                .codigoInterno(" ")
                .activo(true)
                .producto(producto)
                .build();

        if(varianteUnica.getCodigoBarras() != null)
            if(varianteRepository.existsByCodigoBarras(varianteUnica.getCodigoBarras()))
                throw new ResourceDuplicatedException("El código de barras ya se encuentra registrado");

        varianteUnica = varianteRepository.save(varianteUnica);

        varianteUnica.setCodigoInterno(varianteService.generarCodigoInterno(varianteUnica.getIdVariante()));

        varianteRepository.save(varianteUnica);
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
                                "Producto con ID " + idProducto + " no encontrado"
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
                        new ResourceNotFoundException("Producto con ID " + id + " no encontrado"));

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
            throw new IllegalArgumentException(
                    "El precio mayorista no puede ser mayor al precio minorista"
            );
        }

        if (!producto.getTieneVariantes()
                .equals(request.getTieneVariantes())) {
            throw new IllegalArgumentException(
                    "No se puede modificar si un producto posee variantes después de su creación"
            );
        }

        // Actualizar datos editables
        producto.setNombre(request.getNombre().trim());
        
        // Actualizar imagen solo si se proporciona una nueva
        if(request.getImagen() != null && !request.getImagen().isEmpty()) {
            String imagenUrl = cloudinaryService.uploadImage(request.getImagen());
            producto.setImagen(imagenUrl);
        }
        
        producto.setPrecioMinorista(request.getPrecioMinorista());
        producto.setPrecioMayorista(request.getPrecioMayorista());
        producto.setMinimoMayorista(request.getMinimoMayorista());
        producto.setCategoria(categoria);

        return productoMapper.toResponseDTO(producto);
    }

    /**
     * Devuelve todas las configuraciones ProductoCanal del producto enviado
     *
     * @param idProducto Identificador del producto
     * @return Listado de todas las configuraciones asociadas al producto
     */
    @Transactional(readOnly = true)
    public List<ProductoCanalResponseDTO> getChannelsByProduct(Integer idProducto){
        if(!productoRepository.existsById(idProducto)){
            throw new ResourceNotFoundException("El producto con ID " +
            idProducto + " no existe");
        }

        return productoCanalRepository.findByProductoIdProducto(idProducto)
                .stream()
                .map(productoCanalMapper::toResponseDTO)
                .toList();
    }
}
