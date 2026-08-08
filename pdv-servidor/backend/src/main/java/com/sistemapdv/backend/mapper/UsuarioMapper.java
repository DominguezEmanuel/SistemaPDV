package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.request.UsuarioRequestDTO;
import com.sistemapdv.backend.dto.response.UsuarioResponseDTO;
import com.sistemapdv.backend.entity.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UsuarioMapper {

    public Usuario toUsuario(UsuarioRequestDTO dto){

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre().trim())
                .apellido(dto.getApellido().trim())
                .username(dto.getUsername().trim())
                .password(dto.getPassword())
                .rol(dto.getRol())
                .build();

        return usuario;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario){

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .username(usuario.getUsername())
                .activo(usuario.getActivo())
                .rol(usuario.getRol())
                .build();

        return dto;
    }
}
