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

    // Busca un usuario de acuerdo a un username y lo devuelve en caso de encontrarlo
    @GetMapping("/buscar/{username}")
    public ResponseEntity<UsuarioResponseDTO> findByUsername(@PathVariable String username) {
        UsuarioResponseDTO usuarioEncontrado = usuarioService.findByUsername(username);
        logger.info("Usuario {} encontrado", username);
        return ResponseEntity.ok(usuarioEncontrado);
    }

    // Busca un usuario de acuerdo a su ID y lo devuelve en caso de encontrarlo
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Integer id){
        UsuarioResponseDTO usuarioEncontrado = usuarioService.findById(id);
        logger.info("Usuario encontrado con ID {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioEncontrado);
    }

    // ???
    @GetMapping("/verificar/{username}")
    public ResponseEntity<?> verifyUsername(@PathVariable String username){
        if(!usuarioService.verifyUsername(username))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario " + username + " no existe");

        return ResponseEntity.status(HttpStatus.FOUND)
                .body("Usuario " + username + " encontrado");
    }

    // Devuelve la lista de todos los usuarios registrados
    @GetMapping("/")
    @ResponseBody
    public List<UsuarioResponseDTO> findAllUsers(){
        List<UsuarioResponseDTO> usuarios = usuarioService.findAllUsers();
        logger.info("Devolviendo a todos los usuarios");
        return usuarios;
    }

    // Agrega un nuevo usuario al sistema
    @PostMapping("/")
    public ResponseEntity<UsuarioResponseDTO> addUser(@RequestBody UsuarioRequestDTO request){
        UsuarioResponseDTO response = usuarioService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // Activa o desactiva un usuario registrado sin eliminarlo
    @PatchMapping("/estado/{username}")
    public ResponseEntity<UsuarioResponseDTO> setActive(@PathVariable String username, @RequestParam boolean activo){
        UsuarioResponseDTO response = usuarioService.setActiveUser(username, activo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
