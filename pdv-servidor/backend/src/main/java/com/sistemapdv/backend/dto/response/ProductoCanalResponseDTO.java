package com.sistemapdv.backend.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCanalResponseDTO {

    private Integer idProductoCanal;

    private Integer idProducto;

    private String nombreProducto;

    private Integer idCanalVenta;

    private String nombreCanalVenta;

    private Integer limiteMayorista;
}
