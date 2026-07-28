package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.UsuarioRequestDTO;
import com.sistemapdv.backend.dto.UsuarioResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.mapper.UsuarioMapper;
import com.sistemapdv.backend.repository.UsuarioRepository;
import com.sistemapdv.backend.utils.enums.RolUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Usuario findByUsername(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow( ()-> new RuntimeException("Usuario no encontrado (Service)"));
        return usuario;
    }

    public List<Usuario> findAllUsers(){
        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios = usuarioRepository.findAll();
        return usuarios;
    }

    public UsuarioResponseDTO createUser(UsuarioRequestDTO request){
        if(usuarioRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("El nombre de usuario ya existe");

        Usuario usuario = usuarioMapper.toUsuario(request);

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        log.info("Password luego de Bcrypt {}", usuario.getPassword());
        usuario.setActivo(true);

        Usuario newUser = usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(newUser);
    }


    public boolean verifyUsername(String username){
        return usuarioRepository.existsByUsername(username);
    }
}
