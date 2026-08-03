package com.sistemapdv.backend.dto.response;

import com.sistemapdv.backend.utils.enums.RolUsuario;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String username;
    private Boolean activo;
    private RolUsuario rol;
}
