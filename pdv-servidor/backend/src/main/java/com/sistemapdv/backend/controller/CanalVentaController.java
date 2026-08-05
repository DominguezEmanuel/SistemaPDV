package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.CanalRequestDTO;
import com.sistemapdv.backend.dto.response.CanalResponseDTO;
import com.sistemapdv.backend.service.CanalVentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canales")
public class CanalVentaController {

    private final CanalVentaService canalService;

    public CanalVentaController(CanalVentaService canalService) {
        this.canalService = canalService;
    }

    @GetMapping("/")
    @ResponseBody
    public List<CanalResponseDTO> getAllCanales(){
        return canalService.getAllCanales();
    }

    @PostMapping("/")
    public ResponseEntity<CanalResponseDTO> createCanal(@Valid @RequestBody CanalRequestDTO request){
        CanalResponseDTO nuevoCanal = canalService.createCanal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoCanal);
    }
}
