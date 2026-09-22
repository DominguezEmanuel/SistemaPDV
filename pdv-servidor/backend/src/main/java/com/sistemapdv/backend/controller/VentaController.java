package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.VentaRequestDTO;
import com.sistemapdv.backend.dto.response.VentaResponseDTO;
import com.sistemapdv.backend.service.VentaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/")
    public ResponseEntity<List<VentaResponseDTO>> getAllSales(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ventaService.obtenerVentas());
    }

    @PostMapping("/")
    public ResponseEntity<VentaResponseDTO> registerSale(@Valid @RequestBody VentaRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.registrarVenta(request));
    }
}
