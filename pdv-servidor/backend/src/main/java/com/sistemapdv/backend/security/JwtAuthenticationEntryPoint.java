package com.sistemapdv.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemapdv.backend.dto.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .fecha(LocalDateTime.now().toLocalDate().format(formateador))
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .mensaje("Debe iniciar sesión para acceder a este recurso.")
                .path(request.getRequestURI())
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
