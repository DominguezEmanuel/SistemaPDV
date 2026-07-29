package com.sistemapdv.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private LocalDateTime fecha;
    private int status;
    private String error;
    private String mensaje;
    private String path;
}
