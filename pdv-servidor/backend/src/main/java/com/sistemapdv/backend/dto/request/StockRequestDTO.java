package com.sistemapdv.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockRequestDTO {

    @NotNull(message = "Debe indicar la cantidad disponible")
    @Min(value = 0, message = "La cantidad mínima disponible no puede ser menor a 0")
    private Integer cantidadDisponible;

    @NotNull(message = "Debe indicar el stock mínimo de la variante")
    @Min(value = 3, message = "El stock mínimo debe ser mayor a 3")
    private Integer stockMinimo;

    @NotNull(message = "Debe seleccionar una variante de producto")
    private Integer idVariante;

    @NotNull(message = "Debe seleccionar un canal de venta")
    private Integer idCanalVenta;
}
