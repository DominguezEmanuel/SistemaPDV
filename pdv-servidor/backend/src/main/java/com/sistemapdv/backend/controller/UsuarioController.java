package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.UsuarioRequestDTO;
import com.sistemapdv.backend.dto.response.UsuarioResponseDTO;
import com.sistemapdv.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/buscar/{username}")
    public ResponseEntity<UsuarioResponseDTO> findByUsername(@PathVariable String username) {
        UsuarioResponseDTO usuarioEncontrado = usuarioService.findByUsername(username);
        logger.info("Usuario {} encontrado", username);
        return ResponseEntity.ok(usuarioEncontrado);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Integer idUsuario){
        UsuarioResponseDTO usuarioEncontrado = usuarioService.findById(idUsuario);
        logger.info("Usuario encontrado con ID {}", idUsuario);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioEncontrado);
    }

    @GetMapping("/")
    public ResponseEntity<List<UsuarioResponseDTO>> findAllUsers(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.findAllUsers());
    }

    @PostMapping("/")
    public ResponseEntity<UsuarioResponseDTO> addUser(@Valid @RequestBody UsuarioRequestDTO request){
        UsuarioResponseDTO response = usuarioService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/estado/{idUsuario}")
    public ResponseEntity<UsuarioResponseDTO> setUserStatus(@PathVariable Integer idUsuario,
                                                            @RequestParam boolean activo){
        UsuarioResponseDTO usuarioActualizado = usuarioService.setUserStatus(idUsuario, activo);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioActualizado);
    }
}
