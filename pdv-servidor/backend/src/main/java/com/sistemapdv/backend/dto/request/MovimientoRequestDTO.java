package com.sistemapdv.backend.dto.request;

import com.sistemapdv.backend.utils.enums.TipoMovimientoStock;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequestDTO {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimientoStock tipo;

    // Se enviará 'cantidad' o 'stockFisico' de acuerdo al tipo de movimiento
    private Integer cantidad;
    private Integer stockFisico;

    // El 'motivo' es obligatorio solamente para 'SALIDA' y 'AJUSTE'
    private String motivo;
}
