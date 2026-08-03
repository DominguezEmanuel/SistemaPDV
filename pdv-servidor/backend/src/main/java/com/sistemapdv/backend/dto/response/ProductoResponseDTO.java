package com.sistemapdv.backend.dto.response;

import com.sistemapdv.backend.dto.CategoriaDTO;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {

    // Es compatible usar Long??
    private Integer idProducto;
    private String nombre;
    private String imagen;
    private BigDecimal precioMinorista;
    private BigDecimal precioMayorista;
    private Integer minimoMayorista;
    private Boolean activo;
    private CategoriaDTO categoria;
}
