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

    @Transactional(readOnly = true)
    public List<CanalResponseDTO> getAllCanales(){
        List<CanalVenta> canales = canalRepository.findAll();
        return canales.stream()
                .map(canalMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CanalResponseDTO getCanalById(Integer idCanal){
        CanalVenta canal = canalRepository.findById(idCanal)
                .orElseThrow(()-> new ResourceNotFoundException("Canal con ID "
                + idCanal + " no encontrado"));

        return canalMapper.toResponseDTO(canal);
    }

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

    @Transactional
    public CanalResponseDTO updateCanal(Integer idCanal, CanalRequestDTO request){
        CanalVenta canal = canalRepository.findById(idCanal)
                .orElseThrow(()-> new ResourceNotFoundException("Canal con ID "
                        + idCanal + " no encontrado"));

        // Como solamente se puede editar el 'nombre'
        // Solamente se controla que el nuevo nombre no esté registrado
        if(canalRepository.existsByNombreIgnoreCase(request.getNombre().trim()))
            throw new ResourceDuplicatedException("El canal " +
                    request.getNombre() + " ya se encuentra registrado");

        canal.setNombre(request.getNombre().trim());

        return canalMapper.toResponseDTO(canal);
    }
}
