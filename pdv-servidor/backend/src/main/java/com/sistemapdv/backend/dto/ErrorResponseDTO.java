package com.sistemapdv.backend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDTO {
    private String fecha;
    private int status;
    private String error;
    // Para errores simples (404, 409, 401, etc.)
    private String mensaje;
    // Para errores de validación
    private List<String> errores;
    private String path;
}
