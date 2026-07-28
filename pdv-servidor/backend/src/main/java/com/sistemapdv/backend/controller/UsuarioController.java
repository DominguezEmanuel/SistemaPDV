package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.UsuarioRequestDTO;
import com.sistemapdv.backend.dto.UsuarioResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.service.UsuarioService;
import com.sistemapdv.backend.utils.enums.RolUsuario;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<?> findByUsername(@PathVariable String username) {
        try {
            logger.info("Buscando usuario con username: {}", username);
            // Cambiar por DTO
            Usuario usuario = usuarioService.findByUsername(username);

            logger.info("Usuario {} encontrado", username);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            logger.warn("Usuario {} no encontrado (Controller)", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado con username: " + username);
        }
    }

    @GetMapping("/verificar/{username}")
    public ResponseEntity<?> verifyUsername(@PathVariable String username){
        if(!usuarioService.verifyUsername(username))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario " + username + " no existe");

        return ResponseEntity.status(HttpStatus.FOUND)
                .body("Usuario " + username + " encontrado");
    }

    @GetMapping("/")
    @ResponseBody
    public List<Usuario> findAllUsers(){
        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios = usuarioService.findAllUsers();
        logger.info("Devolviendo a todos los usuarios");
        return usuarios;
    }

    @PostMapping("/")
    public ResponseEntity<UsuarioResponseDTO> addUser(@RequestBody UsuarioRequestDTO request){
        UsuarioResponseDTO response = usuarioService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /*
    @GetMapping("/activos/{rol}")
    @ResponseBody
    public List<Usuario> findUsersActivesByRol(@PathVariable RolUsuario rol){
        List<Usuario> usuariosActivos = new ArrayList<Usuario>();
        usuariosActivos = usuarioService.findActivesByRol(rol);
        logger.info("Devolviendo a todos los usuarios activos con el rol {}", rol);
        return usuariosActivos;
    }*/
}
