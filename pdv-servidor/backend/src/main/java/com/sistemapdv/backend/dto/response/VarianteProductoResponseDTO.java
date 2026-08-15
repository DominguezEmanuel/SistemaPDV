package com.sistemapdv.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VarianteProductoResponseDTO {

    private Integer idVariante;
    private String nombre;
    private String codigoBarras;
    private String codigoInterno;
    private Boolean activo;
    private Integer idProducto;
    private String nombreProducto;
    private BigDecimal precioMinorista;
    private BigDecimal precioMayorista;
}
