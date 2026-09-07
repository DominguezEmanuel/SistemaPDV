package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.response.MovimientoResponseDTO;
import com.sistemapdv.backend.service.MovimientoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos-stock")
@AllArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping("/{idStock}/listado")
    public ResponseEntity<List<MovimientoResponseDTO>> getLastFiveRecords(@PathVariable Integer idStock){
        return ResponseEntity.status(HttpStatus.OK)
                .body(movimientoService.getLastFiveRecordsByStock(idStock));
    }

    @GetMapping("/{idMovimiento}")
    public ResponseEntity<MovimientoResponseDTO> getRegisterById(@PathVariable Integer idMovimiento){
        return ResponseEntity.status(HttpStatus.OK)
                .body(movimientoService.getRegisterById(idMovimiento));
    }

}
