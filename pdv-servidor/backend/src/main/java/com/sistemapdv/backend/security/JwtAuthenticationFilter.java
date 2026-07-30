package com.sistemapdv.backend.security;

import com.sistemapdv.backend.exception.ResourceNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por cada request HTTP.
 * 
 * Este filtro:
 * 1. Intercepta cada request entrante
 * 2. Extrae el token JWT del header Authorization
 * 3. Valida que el token sea correcto y no haya expirado
 * 4. Carga el usuario autenticado en el contexto de seguridad de Spring
 * 5. Permite que la request continúe hacia el endpoint
 * 
 * Si el token es inválido, expirado o no existe, la request continúa sin autenticación,
 * y será rechazada por Spring Security en los endpoints que la requieren.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Filtro que se ejecuta para cada request HTTP.
     * 
     * @param request la solicitud HTTP entrante
     * @param response la respuesta HTTP que se enviará al cliente
     * @param filterChain cadena de filtros para continuar con el siguiente filtro
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException si ocurre un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Paso 1: Extraer el token JWT del header Authorization
            String jwt = extractJwtFromRequest(request);

            // Paso 2: Validar que el token exista
            if(jwt == null){
                filterChain.doFilter(request, response);
                return;
            }

            // Paso 3: Extraer el username del token
            String username = jwtService.extractUsername(jwt);

            // Paso 4: Cargar los detalles del usuario desde la base de datos
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Paso 5: Validar que el token sea válido para este usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Paso 6: Crear un token de autenticación con los detalles del usuario
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Paso 7: Establecer detalles de la web (IP, session ID, etc.)
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Paso 8: Guardar la autenticación en el contexto de seguridad
                // Esto permite que Spring Security sepa que el usuario está autenticado
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ResourceNotFoundException e) {
            // Si ocurre cualquier error (token inválido, usuario no encontrado, etc.)
            // simplemente no autenticamos la request y dejamos que continúe
            // Spring Security rechazará la request si el endpoint lo requiere
            logger.error("No se pudo establecer la autenticación del usuario", e);
        }

        // Paso 9: Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization de la request.
     * 
     * El formato esperado es:
     * Authorization: Bearer {token}
     * 
     * Por ejemplo:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * 
     * @param request la solicitud HTTP
     * @return el token JWT sin el prefijo "Bearer ", o null si no existe
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        // Obtener el header Authorization
        String bearerToken = request.getHeader("Authorization");

        // Validar que el header existe y comienza con "Bearer "
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Retornar el token sin el prefijo "Bearer " (elimina los primeros 7 caracteres)
            return bearerToken.substring(7);
        }

        // Si no hay header Authorization o no tiene el formato correcto, retornar null
        return null;
    }
}
