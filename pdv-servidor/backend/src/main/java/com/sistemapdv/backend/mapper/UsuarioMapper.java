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

        Usuario usuario = new Usuario();

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword());
        log.info("Password en Mapper {}", usuario.getPassword());
        usuario.setRol(dto.getRol());

        return usuario;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario){

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setUsername(usuario.getUsername());
        dto.setActivo(usuario.getActivo());
        dto.setRol(usuario.getRol());

        return dto;
    }
}
