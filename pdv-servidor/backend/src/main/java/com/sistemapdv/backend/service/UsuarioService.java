package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.request.UsuarioRequestDTO;
import com.sistemapdv.backend.dto.response.UsuarioResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.UsuarioMapper;
import com.sistemapdv.backend.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByUsername(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow( ()-> new ResourceNotFoundException("Usuario con nombre de usuario '"
                                                                + username + "' no encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Integer id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow( ()-> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAllUsers(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO createUser(UsuarioRequestDTO request){
        // Validar nombre de usuario
        if(usuarioRepository.existsByUsername(request.getUsername()))
            throw new ResourceDuplicatedException("El nombre de usuario '"
                    + request.getUsername() + "' ya se encuentra registrado");

        Usuario usuario = usuarioMapper.toUsuario(request);

        // Hasheo de password
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuario);
    }

    // Activa o desactiva un usuario existente
    @Transactional
    public UsuarioResponseDTO setActiveUser(String username, boolean nuevoEstado){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setActivo(nuevoEstado);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuarioActualizado);
    }
}
