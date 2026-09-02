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
    public List<ProductoCanalResponseDTO> getAllProductsChannels(){
        return productoCanalService.getAllProductsChannels();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoCanalResponseDTO> getProductChannelById(@PathVariable Integer id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoCanalService.getProductChannelById(id));
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
    public ResponseEntity<String> deleteProductChannel(@PathVariable Integer id){
        productoCanalService.deleteProductChannel(id);
        String msg = "El registro con ID " + id + " ha sido eliminado correctamente";
        return ResponseEntity.status(HttpStatus.OK).body(msg);
    }
}
