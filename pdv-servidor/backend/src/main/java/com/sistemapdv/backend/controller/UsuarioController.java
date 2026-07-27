package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.service.UsuarioService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/buscar/{username}")
    public ResponseEntity<?> buscarPorUsername(@PathVariable String username){
        try{
            logger.info("Buscando usuario con username: {}", username);
            // Cambiar por DTO
            Usuario usuario = usuarioService.buscarPorUsername(username);

            logger.info("Usuario {} encontrado", username);
            return ResponseEntity.ok(usuario);
        }catch (Exception e){
            logger.warn("Usuario {} no encontrado (Controller)", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado con username: " + username);
        }
    }
}
