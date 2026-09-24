package com.sistemapdv.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VarianteVentaResponseDTO {

    private Integer idVariante;
    private String nombreVariante;

    private Integer idProducto;
    private String nombreProducto;

    private String codigoBarras;
    private String codigoInterno;

    private BigDecimal precioMinorista;
    private BigDecimal precioMayorista;

    private Integer stockDisponible;
}
