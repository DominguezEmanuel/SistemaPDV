package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.CanalRequestDTO;
import com.sistemapdv.backend.dto.response.CanalResponseDTO;
import com.sistemapdv.backend.entity.CanalVenta;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
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
        return canales.stream().map(canalMapper::toResponseDTO).toList();
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
}
