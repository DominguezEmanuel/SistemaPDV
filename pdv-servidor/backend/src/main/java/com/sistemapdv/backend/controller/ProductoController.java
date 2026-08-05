package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.ProductoRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.dto.response.VarianteProductoResponseDTO;
import com.sistemapdv.backend.service.ProductoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> findById(@PathVariable Integer id){
        ProductoResponseDTO productoEncontrado = productoService.findById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoEncontrado);
    }

    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<ProductoResponseDTO> findByName(@PathVariable String nombre){
        logger.info("Buscando producto con nombre {}", nombre);
        ProductoResponseDTO productoEncontrado = productoService.findByName(nombre);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoEncontrado);
    }

    @GetMapping("/")
    @ResponseBody
    public List<ProductoResponseDTO> findAllProducts(){
        List<ProductoResponseDTO> productos = productoService.findAllProducts();
        return productos;
    }

    @GetMapping("{id}/variantes")
    @ResponseBody
    public List<VarianteProductoResponseDTO> getVariantsByProduct(@PathVariable Integer id){
        List<VarianteProductoResponseDTO> variantes = productoService.getVariantsByProduct(id);
        return variantes;
    }

    @PostMapping("/")
    public ResponseEntity<ProductoResponseDTO> createProduct(
            @Valid @RequestBody ProductoRequestDTO request){
        ProductoResponseDTO nuevoProducto = productoService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoProducto);
    }

    @PatchMapping("/estado/{nombre}")
    public ResponseEntity<ProductoResponseDTO> changeStatus(@PathVariable String nombre,
                                                            @RequestParam boolean activo){
        ProductoResponseDTO response = productoService.changeStatusProduct(nombre, activo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> updateProduct(@PathVariable Integer id,
                                                             @Valid @RequestBody ProductoRequestDTO request){
        ProductoResponseDTO productoActualizado = productoService.updateProduct(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoActualizado);
    }
}
