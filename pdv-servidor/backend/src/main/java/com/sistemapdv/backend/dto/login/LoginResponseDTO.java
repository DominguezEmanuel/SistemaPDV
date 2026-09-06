package com.sistemapdv.backend.dto.login;

import com.sistemapdv.backend.dto.response.UsuarioResponseDTO;
import lombok.*;

/**
 * DTO que se retorna al cliente después de un login exitoso.
 * 
 * Contiene:
 * - token: JWT firmado que el cliente debe usar en futuras requests
 * - usuario: Información del usuario autenticado
 * - tokenType: Tipo de token (siempre "Bearer" para JWT)
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    /**
     * Token JWT firmado.
     * El cliente debe enviar este token en el header Authorization de futuras requests:
     * Authorization: Bearer {token}
     */
    private String token;

    /**
     * Tipo de token.
     * Siempre es "Bearer" en la autenticación JWT.
     */
    private String tokenType;

    /**
     * Información del usuario autenticado.
     * Contiene id, nombre, apellido, username, estado activo y rol.
     */
    private UsuarioResponseDTO usuario;
}
