package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.ProductoCanalRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoCanalResponseDTO;
import com.sistemapdv.backend.service.ProductoCanalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos-canales")
public class ProductoCanalController {

    private final ProductoCanalService productoCanalService;

    public ProductoCanalController(ProductoCanalService productoCanalService) {
        this.productoCanalService = productoCanalService;
    }

    @GetMapping("/")
    @ResponseBody
    public List<ProductoCanalResponseDTO> getAllProductosCanales(){
        return productoCanalService.getAllProductosCanales();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoCanalResponseDTO> getById(@PathVariable Integer id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoCanalService.getProductoCanalById(id));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<ProductoCanalResponseDTO>> findByProducto(@PathVariable Integer idProducto){
        return ResponseEntity.ok(productoCanalService.findByProducto(idProducto));
    }

    @GetMapping("/canal/{idCanal}")
    public ResponseEntity<List<ProductoCanalResponseDTO>> findByCanal(@PathVariable Integer idCanal){
        return ResponseEntity.ok(productoCanalService.findByCanal(idCanal));
    }

    @PostMapping("/")
    public ResponseEntity<ProductoCanalResponseDTO> addProductoCanal(
            @Valid @RequestBody ProductoCanalRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoCanalService.saveProductoCanal(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductoCanalResponseDTO> updateLimiteMayorista(@PathVariable Integer id,
                                                                        @RequestParam Integer nuevoLimite){
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoCanalService.updateLimiteMayorista(id, nuevoLimite));
    }
}
