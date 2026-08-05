package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.response.CanalResponseDTO;
import com.sistemapdv.backend.entity.CanalVenta;
import org.springframework.stereotype.Component;

@Component
public class CanalVentaMapper {

    public CanalResponseDTO toResponseDTO(CanalVenta canal){
        return CanalResponseDTO.builder()
                .idCanalVenta(canal.getIdCanalVenta())
                .nombre(canal.getNombre())
                .build();
    }
}
