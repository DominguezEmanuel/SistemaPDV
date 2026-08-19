package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.VarianteProductoRequestDTO;
import com.sistemapdv.backend.dto.response.VarianteProductoResponseDTO;
import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.VarianteProducto;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.VarianteProductoMapper;
import com.sistemapdv.backend.repository.ProductoRepository;
import com.sistemapdv.backend.repository.VarianteProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class VarianteProductoService {

    private final VarianteProductoRepository varianteProductoRepository;
    private final ProductoRepository productoRepository;
    private final VarianteProductoMapper varianteMapper;

    public VarianteProductoService(VarianteProductoRepository varianteProductoRepository, ProductoRepository productoRepository, VarianteProductoMapper varianteMapper) {
        this.varianteProductoRepository = varianteProductoRepository;
        this.productoRepository = productoRepository;
        this.varianteMapper = varianteMapper;
    }

    @Transactional(readOnly = true)
    public List<VarianteProductoResponseDTO> getAllVariants(){
        List<VarianteProducto> variantes = varianteProductoRepository.findAll();
        return variantes.stream()
                .map(varianteMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VarianteProductoResponseDTO getVariantById(Integer id){
        VarianteProducto variante = varianteProductoRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Variante con ID " + id + " no encontrada"));
        return varianteMapper.toResponseDTO(variante);
    }

    @Transactional
    public VarianteProductoResponseDTO createVariant(VarianteProductoRequestDTO request){
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Producto con ID " + request.getIdProducto() + " no encontrado"));

        if(!producto.getTieneVariantes())
            throw new IllegalArgumentException("Este producto está configurado para no aceptar variantes");

        if(request.getCodigoBarras() != null)
            if(varianteProductoRepository.existsByCodigoBarras(request.getCodigoBarras()))
                throw new ResourceDuplicatedException("El código de barras " +
                        request.getCodigoBarras() + " ya se encuentra registrado");


        VarianteProducto variante = varianteMapper.toVariante(request, producto);

        varianteProductoRepository.save(variante);

        variante.setCodigoInterno(generarCodigoInterno(variante.getIdVariante()));

        return varianteMapper.toResponseDTO(variante);
    }

    public String generarCodigoInterno(Integer idVariante){
        return String.format("VAR-%06d", idVariante);
    }

    @Transactional
    public VarianteProductoResponseDTO updateVariant(Integer idVariante,
                                                     VarianteProductoRequestDTO request) {
        // Validar que exista el ID de variante
        VarianteProducto variante = varianteProductoRepository.findById(idVariante)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Variante con ID " + idVariante +
                                " no encontrada"));

        if (!productoRepository.existsById(request.getIdProducto()))
            throw new ResourceNotFoundException("Producto con ID " +
                    request.getIdProducto() + " no encontrado");


        log.info("Actualizando variante ID: {}", idVariante);
        log.info("Producto asociado actualmente a la variante: {}",
                variante.getProducto().getIdProducto());
        log.info("Producto recibido en la request: {}",
                request.getIdProducto());

        // Verificar que la variante le sigue perteneciendo al mismo producto
        if (!variante.getProducto().getIdProducto().equals(request.getIdProducto()))
            throw new IllegalArgumentException("No es posible cambiar el producto asociado a la variante");

        log.info("Código de barras nuevo {}", request.getCodigoBarras());
        log.info("Código de barras actual {}", variante.getCodigoBarras());

        // Verificar que el código de barras no se encuentre registrado
        if (request.getCodigoBarras() != null)
            if (varianteProductoRepository.existsByCodigoBarras(request.getCodigoBarras())
                    && !variante.getCodigoBarras().equals(request.getCodigoBarras())) {
                throw new ResourceDuplicatedException("El código de barras " +
                        request.getCodigoBarras() + " ya se encuentra registrado");
            }

        variante.setNombre(request.getNombre().trim());
        variante.setCodigoBarras(request.getCodigoBarras());

        return varianteMapper.toResponseDTO(variante);
    }

    @Transactional
    public VarianteProductoResponseDTO changeStatusVariant(Integer id, boolean activo){
        VarianteProducto variante = varianteProductoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Variante con ID " + id + " no encontrada"));

        if(variante.getActivo().equals(activo))
            throw new IllegalArgumentException("La variante ya se encuentra "
                        + (activo ? "habilitada" : "deshabilitada"));

        variante.setActivo(activo);

        return varianteMapper.toResponseDTO(variante);
    }

    @Transactional(readOnly = true)
    public VarianteProductoResponseDTO findByBarCode(String codigoBarras){
        VarianteProducto variante = varianteProductoRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(()-> new ResourceNotFoundException("Variante con código "
                + codigoBarras + " no encontrada"));
        return varianteMapper.toResponseDTO(variante);
    }

    @Transactional(readOnly = true)
    public VarianteProductoResponseDTO findByInternCode(String codigoInterno){
        VarianteProducto variante = varianteProductoRepository.findByCodigoInterno(codigoInterno)
                .orElseThrow(()-> new ResourceNotFoundException("Variante con código "
                + codigoInterno + " no encontrada"));
        return varianteMapper.toResponseDTO(variante);
    }
}
