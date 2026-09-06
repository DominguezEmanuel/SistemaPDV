package com.sistemapdv.backend.service;

import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;

    public AuthenticationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getUserAuthenticated(){

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = authentication.getName();

        return usuarioRepository
                .findByUsername(username)
                .orElseThrow( () -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }
}
