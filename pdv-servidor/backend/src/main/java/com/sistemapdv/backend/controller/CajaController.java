package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.CajaDTO;
import com.sistemapdv.backend.service.CajaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @PostMapping("/")
    public ResponseEntity<CajaDTO> createCash(@Valid @RequestBody CajaDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cajaService.createCash(request));
    }
}
