package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.ProductoRequestDTO;
import com.sistemapdv.backend.dto.response.ProductoResponseDTO;
import com.sistemapdv.backend.dto.response.VarianteProductoResponseDTO;
import com.sistemapdv.backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

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
        ProductoResponseDTO productoEncontrado = productoService.findByName(nombre);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoEncontrado);
    }

    @GetMapping("/buscar")
    public PagedModel<ProductoResponseDTO> findByFilters(@RequestParam(required = false) String nombre,
                                                   @RequestParam(required = false) Integer idCategoria,
                                                   @RequestParam(required = false) Boolean activo,
                                                   @PageableDefault(sort = "nombre", direction = Sort.Direction.ASC)
                                                       Pageable pageable){
        Page<ProductoResponseDTO> productosFiltrados = productoService.filterByFilters(nombre, idCategoria, activo, pageable);

        return new PagedModel<>(productosFiltrados);
    }

    @GetMapping("/")
    public PagedModel<ProductoResponseDTO> findAllProducts(@PageableDefault(sort = "nombre", direction = Sort.Direction.ASC)
                                                         Pageable pageable){
        Page<ProductoResponseDTO> productos = productoService.findAllProducts(pageable);
        return new PagedModel<>(productos);
    }

    @GetMapping("{id}/variantes")
    @ResponseBody
    public List<VarianteProductoResponseDTO> getVariantsByProduct(@PathVariable Integer id){
        List<VarianteProductoResponseDTO> variantes = productoService.getVariantsByProduct(id);
        return variantes;
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDTO> createProduct(
            @Valid @ModelAttribute ProductoRequestDTO request){
        ProductoResponseDTO nuevoProducto = productoService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoProducto);
    }

    @PatchMapping("/estado/{idProducto}")
    public ResponseEntity<ProductoResponseDTO> changeStatus(@PathVariable Integer idProducto,
                                                            @RequestParam Boolean activo){
        ProductoResponseDTO response = productoService.changeStatusProduct(idProducto, activo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/imagen/{idProducto}")
    public ResponseEntity<ProductoResponseDTO> updateImage(@PathVariable Integer idProducto,
                                                           @RequestParam MultipartFile imagen){
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoService.updateImage(idProducto, imagen));
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDTO> updateProduct(@PathVariable Integer id,
                                                             @Valid @ModelAttribute ProductoRequestDTO request){
        ProductoResponseDTO productoActualizado = productoService.updateProduct(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productoActualizado);
    }
}
