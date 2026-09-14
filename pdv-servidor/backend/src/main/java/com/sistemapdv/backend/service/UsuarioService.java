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

    /**
     * Busca un usuario registrado de acuerdo a su 'username'
     *
     * @param username Nombre de usuario del usuario buscado
     * @return Usuario encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByUsername(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow( ()-> new ResourceNotFoundException("Usuario con nombre de usuario '"
                                                                + username + "' no encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Devuelve un usuario de acuerdo a su ID
     *
     * @param id Identificador del usuario
     * @return Usuario encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Integer id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow( ()-> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Devuelve el listado de usuarios registrados
     *
     * @return Listado de usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAllUsers(){
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Registra un nuevo usuario en la base de datos
     *
     * @param request Solicitud que contiene los datos necesarios para el alta de usuario
     * @return Usuario creado
     */
    @Transactional
    public UsuarioResponseDTO createUser(UsuarioRequestDTO request){
        // Validar nombre de usuario no registrado
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

    /**
     * Cambia el estado de un usuario de 'activo' a 'inactivo' o viceversa
     *
     * @param idUsuario Identificador del usuario del usuario que cambiará de estado
     * @param nuevoEstado Nuevo estado del usuario
     * @return Usuario con estado actualizado
     */
    @Transactional
    public UsuarioResponseDTO setUserStatus(Integer idUsuario, boolean nuevoEstado){
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID "
                        + idUsuario + " no encontrado"));

        if(usuario.getActivo().equals(nuevoEstado))
            throw new IllegalArgumentException("El usuario ya se encuentra "
            + (usuario.getActivo() ? "activo" : "inactivo"));

        usuario.setActivo(nuevoEstado);

        return usuarioMapper.toResponseDTO(usuario);
    }
}
