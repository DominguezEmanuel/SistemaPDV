package com.sistemapdv.backend.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VarianteProductoResponseDTO {

    private Integer idVariante;
    private String nombre;
    private String codigoBarras;
    private Boolean activo;
    private Integer idProducto;
    private String nombreProducto;
}
