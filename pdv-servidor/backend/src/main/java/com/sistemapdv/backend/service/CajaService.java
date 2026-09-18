package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.CajaDTO;
import com.sistemapdv.backend.entity.Caja;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.mapper.CajaMapper;
import com.sistemapdv.backend.repository.CajaRepository;
import com.sistemapdv.backend.utils.enums.EstadoCaja;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class CajaService {

    private final AuthenticationService authenticationService;
    private final CajaRepository cajaRepository;
    private final CajaMapper cajaMapper;

    public CajaService(AuthenticationService authenticationService, CajaRepository cajaRepository, CajaMapper cajaMapper) {
        this.authenticationService = authenticationService;
        this.cajaRepository = cajaRepository;
        this.cajaMapper = cajaMapper;
    }

    @Transactional
    public CajaDTO createCash(CajaDTO request){

        Usuario usuarioAutenticado = authenticationService.getUserAuthenticated();

        Caja nuevaCaja = new Caja();

        nuevaCaja.setUsuario(usuarioAutenticado);
        nuevaCaja.setFechaApertura(OffsetDateTime.now());
        nuevaCaja.setMontoInicial(request.getMontoInicial());
        nuevaCaja.setEstado(EstadoCaja.ABIERTA);

        cajaRepository.save(nuevaCaja);

        return cajaMapper.toResponseDTO(nuevaCaja);
    }

}
