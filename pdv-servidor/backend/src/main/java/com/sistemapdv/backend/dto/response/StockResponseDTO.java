package com.sistemapdv.backend.dto.response;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {

    private Integer idStock;

    private Integer cantidadDisponible;

    private Integer stockMinimo;

    private Integer idVariante;

    private String nombreVariante;

    private Integer idCanalVenta;

    private String canalVenta;
}
