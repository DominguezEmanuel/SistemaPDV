package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.UsuarioRequestDTO;
import com.sistemapdv.backend.dto.UsuarioResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.UsuarioMapper;
import com.sistemapdv.backend.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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

    public UsuarioResponseDTO findByUsername(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow( ()-> new ResourceNotFoundException("Usuario no encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }

    public UsuarioResponseDTO findById(Integer id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow( ()-> new ResourceNotFoundException("Usuario no encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }

    public List<UsuarioResponseDTO> findAllUsers(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        //List<Usuario> usuarios = usuarioRepository.findAllByOrderByIdAsc();
        List<UsuarioResponseDTO> listadoUsuarios = new ArrayList<UsuarioResponseDTO>();
        for (Usuario u: usuarios){
            listadoUsuarios.add(usuarioMapper.toResponseDTO(u));
        }
        return listadoUsuarios;
    }

    public UsuarioResponseDTO createUser(UsuarioRequestDTO request){
        if(usuarioRepository.existsByUsername(request.getUsername()))
            throw new ResourceDuplicatedException("El nombre de usuario ya existe");

        Usuario usuario = usuarioMapper.toUsuario(request);

        // Hasheo de password
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setActivo(true);

        Usuario usuarioCreado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuarioCreado);
    }

    // ????
    public boolean verifyUsername(String username){
        return usuarioRepository.existsByUsername(username);
    }

    // Activa o desactiva un usuario existente
    public UsuarioResponseDTO setActiveUser(String username, boolean nuevoEstado){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setActivo(nuevoEstado);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuarioActualizado);
    }
}
