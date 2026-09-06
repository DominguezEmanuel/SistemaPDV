package com.sistemapdv.backend.dto.request;

import com.sistemapdv.backend.utils.enums.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequestDTO {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    private Integer cantidad;

    private Integer stockFisico;

    private String motivo;
}
