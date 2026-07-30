package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.UsuarioResponseDTO;
import com.sistemapdv.backend.dto.login.LoginRequestDTO;
import com.sistemapdv.backend.dto.login.LoginResponseDTO;
import com.sistemapdv.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.login(loginRequest));
    }
}
