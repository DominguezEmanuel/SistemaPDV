package com.sistemapdv.backend.dto;

import com.sistemapdv.backend.utils.enums.RolUsuario;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
    Integer idUsuario;
    String nombre;
    String apellido;
    String username;
    Boolean activo;
    RolUsuario rol;
}
