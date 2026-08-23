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

    private Integer idProducto;
    private String nombreProducto;

    private Integer idVariante;
    private String nombreVariante;
    private String codigoBarras;
    private String codigoInterno;

    private Integer idCanalVenta;
    private String nombreCanalVenta;
}
