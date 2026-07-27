package com.sistemapdv.backend.service;

import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorUsername(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow( ()-> new RuntimeException("Usuario no encontrado (Service)"));
        return usuario;
    }
}
