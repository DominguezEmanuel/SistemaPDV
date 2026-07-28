package com.sistemapdv.backend.dto;

import com.sistemapdv.backend.utils.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Esta clase DTO representa la información que se envía al frontend
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    //Integer idUsuario;
    String nombre;
    String apellido;
    String username;
    String password;
    //Boolean activo;
    RolUsuario rol;
}
