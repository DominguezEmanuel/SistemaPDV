package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.VarianteProductoRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.dto.response.VarianteProductoResponseDTO;
import com.sistemapdv.backend.service.VarianteProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variantes")
public class VarianteController {

    private final VarianteProductoService varianteService;

    public VarianteController(VarianteProductoService varianteService) {
        this.varianteService = varianteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VarianteProductoResponseDTO> getVariantById(@PathVariable Integer id){
        VarianteProductoResponseDTO varianteEncontrada = varianteService.getVariantById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(varianteEncontrada);
    }

    @GetMapping("/")
    @ResponseBody
    public List<VarianteProductoResponseDTO> getAllVariants(){
        List<VarianteProductoResponseDTO> variantes = varianteService.getAllVariants();
        return variantes;
    }

    @GetMapping("/codigo-barras/{codigoBarras}")
    public ResponseEntity<VarianteProductoResponseDTO> getVariantByBarCode(@PathVariable String codigoBarras){
        VarianteProductoResponseDTO varianteEncontrada = varianteService.findByBarCode(codigoBarras);
        return ResponseEntity.status(HttpStatus.OK)
                .body(varianteEncontrada);
    }

    @GetMapping("/codigo-interno/{codigoInterno}")
    public ResponseEntity<VarianteProductoResponseDTO> getVariantByInternCode(@PathVariable String codigoInterno){
        VarianteProductoResponseDTO varianteEncontrada = varianteService.findByInternCode(codigoInterno);
        return ResponseEntity.status(HttpStatus.OK)
                .body(varianteEncontrada);
    }

    @PostMapping("/")
    public ResponseEntity<VarianteProductoResponseDTO> createVariant(
            @Valid @RequestBody VarianteProductoRequestDTO request){
        VarianteProductoResponseDTO response = varianteService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VarianteProductoResponseDTO> updateVariant(@PathVariable Integer id,
                                                                     @Valid @RequestBody VarianteProductoRequestDTO request){
        VarianteProductoResponseDTO varianteActualizada = varianteService.updateVariant(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(varianteActualizada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VarianteProductoResponseDTO> changeStatusVariant(@PathVariable Integer id,
                                                                           @RequestParam boolean activo){
        VarianteProductoResponseDTO variante = varianteService.changeStatusVariant(id, activo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(variante);
    }
}
