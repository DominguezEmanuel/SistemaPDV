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

    /**
     * Devuelve todos los registros de ProductoCanal
     *
     * @return Listado de ProductoCanal
     */
    @Transactional(readOnly = true)
    public List<ProductoCanalResponseDTO> getAllProductsChannels(){
        List<ProductoCanal> productosCanales = repository.findAll();
        return productosCanales
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Devuelve un registro Producto-Canal de acuerdo a los ID's enviados
     *
     * @param idProducto Identifiacdor del producto
     * @param idCanal Identificador del canal de venta
     * @return Registro que pertenece a la configuración idProducto + idCanal
     */
    @Transactional(readOnly = true)
    public ProductoCanalResponseDTO getByIdProductAndIdChannel(Integer idProducto, Integer idCanal){
        // Validar producto existente
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Producto con ID " + idProducto
                                + " no encontrado"));
        // Validar canal de venta existente
        CanalVenta canal = canalVentaRepository.findById(idCanal)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Canal de venta con ID " + idCanal
                                + " no encontrado"));
        // Verificar la existencia del registro de Producto + Canal
        ProductoCanal registro = repository.findByProductoIdProductoAndCanalVentaIdCanalVenta(idProducto, idCanal)
                .orElseThrow(()-> new ResourceNotFoundException("Registro para " +
                        producto.getNombre() + " y " + canal.getNombre() + " no encontrado"));

        return mapper.toResponseDTO(registro);
    }

    /**
     * Devuelve un registro ProductoCanal
     *
     * @param id Identificador del registro del que se desea información
     * @return Registro ProductoCanal solicitado
     */
    @Transactional(readOnly = true)
    public ProductoCanalResponseDTO getProductChannelById(Integer id){
        ProductoCanal productoCanal = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Configuración con ID "
                + id + " no encontrada"));
        return mapper.toResponseDTO(productoCanal);
    }

    /**
     * Agrega un nuevo registro de ProductoCanal
     *
     * @param request Solicitud con los datos necesarios para el registro
     * @return Nuevo registro ya almacenado en la base de datos
     */
    @Transactional
    public ProductoCanalResponseDTO createProductChannel(ProductoCanalRequestDTO request){
        // Validar producto existente
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Producto con ID " + request.getIdProducto()
                                + " no encontrado"));
        // Validar canal de venta existente
        CanalVenta canal = canalVentaRepository.findById(request.getIdCanalVenta())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Canal de venta con ID " + request.getIdCanalVenta()
                                + " no encontrado"));
        // Validar que no exista la configuración Producto + CanalVenta
        if(repository.existsByProductoIdProductoAndCanalVentaIdCanalVenta(
                request.getIdProducto(),
                request.getIdCanalVenta())){
            throw new ResourceDuplicatedException("Ya existe una configuración para "
                + producto.getNombre() + " y " + canal.getNombre());
        }
        // Crear el registro y guardarlo
        ProductoCanal entity = ProductoCanal.builder()
                .producto(producto)
                .canalVenta(canal)
                .limiteMayorista(request.getLimiteMayorista())
                .build();

        return mapper.toResponseDTO(repository.save(entity));
    }

    /**
     * Modifica el atributo 'limiteMayorista' del registro de ProductoCanal
     *
     * @param id Identificador del registro que se desea modificar
     * @param nuevoLimite Nuevo valor para el campo 'limiteMayorisya' del registro
     * @return Registro actualizado
     */
    @Transactional
    public ProductoCanalResponseDTO updateLimiteMayorista(Integer id, Integer nuevoLimite){

        ProductoCanal entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración con ID " +
                        id + " no encontrada"));

        if(nuevoLimite < 1)
            throw new IllegalArgumentException("El limite mayorista debe ser mayor o igual a 1");

        if(entity.getLimiteMayorista().compareTo(nuevoLimite) == 0)
            throw new IllegalArgumentException("El registro ya tiene configurado a " + nuevoLimite
                                                + " como limite mayorista");

        entity.setLimiteMayorista(nuevoLimite);

        return mapper.toResponseDTO(entity);
    }

    /**
     * Elimina un registro ProductoCanal de la base de datos
     *
     * @param id Identificador del registro que se desea eliminar
     */
    @Transactional
    public void deleteProductChannel(Integer id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Configuración con ID "
                    + id + " no encontrada");
        }

        repository.deleteById(id);
    }
}
