package com.sistemapdv.backend.dto.response;

import com.sistemapdv.backend.utils.enums.TipoMovimiento;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponseDTO {

    private Integer idMovimiento;
    private String fechaHora;
    private TipoMovimiento tipo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockResultante;
    private String motivo;
    private Integer idUsuario;
    private Integer idStock;
}
