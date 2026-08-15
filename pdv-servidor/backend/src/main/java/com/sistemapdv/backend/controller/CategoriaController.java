package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.CategoriaDTO;
import com.sistemapdv.backend.entity.Categoria;
import com.sistemapdv.backend.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> findById(@PathVariable Integer id){
        CategoriaDTO categoria = categoriaService.findById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoria);
    }

    @GetMapping("/")
    @ResponseBody
    public List<CategoriaDTO> getAllCategories(){
        List<CategoriaDTO> categorias = categoriaService.findAllCategories();
        return categorias;
    }

    @PostMapping("/")
    public ResponseEntity<CategoriaDTO> createCategory(@RequestBody CategoriaDTO request){
        CategoriaDTO response = categoriaService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
