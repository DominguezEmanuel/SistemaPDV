package com.sistemapdv.backend.dto.login;

import com.sistemapdv.backend.utils.enums.RolUsuario;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String username;
    private RolUsuario rol;

    //private Boolean activo;
    //private String tokenJWT;
}
