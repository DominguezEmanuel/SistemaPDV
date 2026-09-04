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
    public ResponseEntity<List<ProductoCanalResponseDTO>> getAllProductsChannels(){
        return ResponseEntity.status(HttpStatus.OK).body(productoCanalService.getAllProductsChannels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoCanalResponseDTO> getProductChannelById(@PathVariable Integer id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoCanalService.getProductChannelById(id));
    }

    @GetMapping("/producto/{idProducto}/canal/{idCanalVenta}")
    public ResponseEntity<ProductoCanalResponseDTO> getByProductAndChannel(@PathVariable Integer idProducto,
                                                                           @PathVariable Integer idCanalVenta){
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoCanalService.getByIdProductAndIdChannel(idProducto, idCanalVenta));
    }

    @PostMapping("/")
    public ResponseEntity<ProductoCanalResponseDTO> createProductChannel(
            @Valid @RequestBody ProductoCanalRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoCanalService.createProductChannel(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductoCanalResponseDTO> updateLimiteMayorista(@PathVariable Integer id,
                                                                          @RequestParam Integer nuevoLimite){
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoCanalService.updateLimiteMayorista(id, nuevoLimite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductChannel(@PathVariable Integer id){
        productoCanalService.deleteProductChannel(id);
        return ResponseEntity.noContent().build();
    }
}
