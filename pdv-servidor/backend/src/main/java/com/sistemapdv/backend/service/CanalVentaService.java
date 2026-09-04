package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.CanalRequestDTO;
import com.sistemapdv.backend.dto.response.CanalResponseDTO;
import com.sistemapdv.backend.entity.CanalVenta;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.CanalVentaMapper;
import com.sistemapdv.backend.repository.CanalVentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CanalVentaService {

    private final CanalVentaRepository canalRepository;
    private final CanalVentaMapper canalMapper;

    public CanalVentaService(CanalVentaRepository canalRepository, CanalVentaMapper canalMapper) {
        this.canalRepository = canalRepository;
        this.canalMapper = canalMapper;
    }

    /**
     * Devuelve todos los canales de venta registrados
     *
     * @return Listado de canales de venta
     */
    @Transactional(readOnly = true)
    public List<CanalResponseDTO> getAllCanales(){
        List<CanalVenta> canales = canalRepository.findAll();
        return canales.stream()
                .map(canalMapper::toResponseDTO)
                .toList();
    }

    /**
     * Retorna un canal de venta de acuerdo a un ID enviado
     *
     * @param idCanal Identificador del canal buscado
     * @return Canal de venta registrado en la base de datos
     */
    @Transactional(readOnly = true)
    public CanalResponseDTO getCanalById(Integer idCanal){
        CanalVenta canal = canalRepository.findById(idCanal)
                .orElseThrow(()-> new ResourceNotFoundException("Canal con ID "
                + idCanal + " no encontrado"));

        return canalMapper.toResponseDTO(canal);
    }

    /**
     * Agrega un nuevo canal de venta en la base de datos
     *
     * @param request Datos necesarios para la creación de un nuevo canal
     * @return Canal de venta ya creado
     */
    @Transactional
    public CanalResponseDTO createCanal(CanalRequestDTO request){
        // Verificar que el nombre del canal no se encuentre registrado
        if(canalRepository.existsByNombreIgnoreCase(request.getNombre()))
            throw new ResourceDuplicatedException("El canal " +
                    request.getNombre() + " ya se encuentra registrado");

        CanalVenta nuevoCanal = CanalVenta.builder()
                .nombre(request.getNombre().trim())
                .build();

        canalRepository.save(nuevoCanal);

        return canalMapper.toResponseDTO(nuevoCanal);
    }

    /**
     * Edita el campo 'nombre' de un canal de venta
     *
     * @param idCanal Identificador del canal de venta que se desea edutar
     * @param request Solicitud con el nuevo nombre del canal de venta
     * @return Canal de venta con el 'nombre' actualizado
     */
    @Transactional
    public CanalResponseDTO updateCanal(Integer idCanal, CanalRequestDTO request){
        // Verifica que el canal exista
        CanalVenta canal = canalRepository.findById(idCanal)
                .orElseThrow(()-> new ResourceNotFoundException("Canal con ID "
                        + idCanal + " no encontrado"));

        // Comprobar si tiene el mismo nombre
        if(canal.getNombre().equalsIgnoreCase(request.getNombre())){
            throw new IllegalArgumentException("Esta intentando guardar el mismo nombre");
        }

        // Como solamente se puede editar el 'nombre'
        // Solamente se controla que el nuevo nombre no esté registrado
        if(canalRepository.existsByNombreIgnoreCase(request.getNombre().trim()))
            throw new ResourceDuplicatedException("El canal " +
                    request.getNombre() + " ya se encuentra registrado");

        canal.setNombre(request.getNombre().trim());

        return canalMapper.toResponseDTO(canal);
    }
}
