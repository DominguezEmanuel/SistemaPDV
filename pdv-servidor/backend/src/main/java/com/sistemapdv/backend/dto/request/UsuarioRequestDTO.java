package com.sistemapdv.backend.dto.request;

import com.sistemapdv.backend.utils.enums.RolUsuario;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    private String nombre;
    private String apellido;
    private String username;
    private String password;
    private RolUsuario rol;
}
