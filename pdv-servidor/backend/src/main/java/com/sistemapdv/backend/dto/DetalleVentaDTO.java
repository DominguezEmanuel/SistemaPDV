package com.sistemapdv.backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleVentaDTO {
    private Integer idDetalleVenta;

    private Integer idVariante;
    private String nombreVariante;

    private String nombreProducto;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
