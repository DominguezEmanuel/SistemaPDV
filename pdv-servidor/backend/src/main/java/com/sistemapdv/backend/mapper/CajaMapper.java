package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.CajaDTO;
import com.sistemapdv.backend.entity.Caja;
import com.sistemapdv.backend.utils.FormatterDates;
import org.springframework.stereotype.Component;

@Component
public class CajaMapper {

    public CajaDTO toResponseDTO(Caja caja){
        CajaDTO dto = CajaDTO.builder()
                .idCaja(caja.getIdCaja())
                .idUsuario(caja.getUsuario().getIdUsuario())
                .username(caja.getUsuario().getUsername())
                .fechaApertura(FormatterDates.formatearFechaRegistro(caja.getFechaApertura()))
                .fechaCierre(FormatterDates.formatearFechaRegistro(caja.getFechaCierre()))
                .montoInicial(caja.getMontoInicial())
                .montoFinal(caja.getMontoFinal())
                .saldoEsperado(caja.getSaldoEsperado())
                .estado(caja.getEstado())
                .build();
        return dto;
    }
}
