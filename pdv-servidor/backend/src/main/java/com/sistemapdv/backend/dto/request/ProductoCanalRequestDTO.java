package com.sistemapdv.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCanalRequestDTO {

    @NotNull(message = "Debe seleccionar un producto")
    private Integer idProducto;

    @NotNull(message = "Debe seleccionar un canal de venta")
    private Integer idCanalVenta;

    @NotNull(message = "El limite mayorista es obligatorio")
    @Min(value = 1, message = "El limite mayorista debe ser al menos 1")
    private Integer limiteMayorista;
}
