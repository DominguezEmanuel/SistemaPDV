package com.sistemapdv.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDTO {
    private String fecha;
    private int status;
    private String error;
    private String mensaje;
    private String path;
}
