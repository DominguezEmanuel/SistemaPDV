package com.sistemapdv.backend.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanalResponseDTO {
    private Integer idCanalVenta;
    private String nombre;
}
