package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.ProductoCanalRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoCanalResponseDTO;
import com.sistemapdv.backend.entity.CanalVenta;
import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.ProductoCanal;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.ProductoCanalMapper;
import com.sistemapdv.backend.repository.CanalVentaRepository;
import com.sistemapdv.backend.repository.ProductoCanalRepository;
import com.sistemapdv.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoCanalService {

    private final ProductoCanalRepository repository;
    private final ProductoRepository productoRepository;
    private final CanalVentaRepository canalVentaRepository;
    private final ProductoCanalMapper mapper;

    public ProductoCanalService(ProductoCanalRepository repository,
                                ProductoRepository productoRepository,
                                CanalVentaRepository canalVentaRepository,
                                ProductoCanalMapper mapper) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.canalVentaRepository = canalVentaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProductoCanalResponseDTO> getAllProductosCanales(){
        List<ProductoCanal> productosCanales = repository.findAll();
        return productosCanales
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoCanalResponseDTO getProductoCanalById(Integer id){
        ProductoCanal productoCanal = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Configuración no encontrada"));
        return mapper.toResponseDTO(productoCanal);
    }

    @Transactional(readOnly = true)
    public List<ProductoCanalResponseDTO> findByProducto(Integer idProducto){

        if(!productoRepository.existsById(idProducto))
            throw new ResourceNotFoundException("Producto con ID " + idProducto + " no encontrado");

        return repository.findByProductoIdProducto(idProducto)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoCanalResponseDTO> findByCanal(Integer idCanal){

        if(!canalVentaRepository.existsById(idCanal))
            throw new ResourceNotFoundException("Canal Venta con ID " + idCanal + " no encontrado");

        return repository.findByCanalVentaIdCanalVenta(idCanal)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoCanalResponseDTO findByCanalAndProducto(Integer idCanal, Integer idProducto){

        if(!productoRepository.existsById(idProducto))
            throw new ResourceNotFoundException("Producto con ID " + idProducto + " no encontrado");

        if(!canalVentaRepository.existsById(idCanal))
            throw new ResourceNotFoundException("Canal Venta con ID " + idCanal + " no encontrado");

        ProductoCanal productoCanal = repository.findByProductoIdProductoAndCanalVentaIdCanalVenta(idProducto, idCanal)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado para Canal "
                + idCanal + " Producto " + idProducto));

        return mapper.toResponseDTO(productoCanal);
    }

    @Transactional
    public ProductoCanalResponseDTO saveProductoCanal(ProductoCanalRequestDTO request){

        if(repository.existsByProductoIdProductoAndCanalVentaIdCanalVenta(
                request.getIdProducto(),
                request.getIdCanalVenta())){
            throw new ResourceDuplicatedException("Ya existe una configuración para ese producto y canal");
        }

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Producto no encontrado"));

        CanalVenta canal = canalVentaRepository.findById(request.getIdCanalVenta())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Canal de venta no encontrado"));

        ProductoCanal entity = ProductoCanal.builder()
                .producto(producto)
                .canalVenta(canal)
                .limiteMayorista(request.getLimiteMayorista())
                .build();

        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public ProductoCanalResponseDTO updateLimiteMayorista(Integer id,
                                                          Integer nuevoLimite){

        ProductoCanal entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Configuración no encontrada"));

        if(nuevoLimite < 1)
            throw new IllegalArgumentException("El limite mayorista debe ser al menos 1");

        if(entity.getLimiteMayorista().compareTo(nuevoLimite) == 0)
            throw new IllegalArgumentException("El producto ya tiene el limite mayorista de "
                                                + nuevoLimite);

        entity.setLimiteMayorista(nuevoLimite);

        return mapper.toResponseDTO(repository.save(entity));
    }
}
