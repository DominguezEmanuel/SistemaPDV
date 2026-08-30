package com.sistemapdv.backend.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockProductoResponseDTO {

    private Integer idStock;

    private Integer idVariante;
    private String nombreVariante;

    private Integer idCanalVenta;
    private String nombreCanalVenta;

    private Integer cantidadDisponible;
    private Integer stockMinimo;
}
