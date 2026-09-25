package com.sistemapdv.backend.dto.response;

import com.sistemapdv.backend.utils.enums.TipoMovimientoStock;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponseDTO {

    private Integer idMovimiento;
    private String fechaHora;
    private TipoMovimientoStock tipo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockResultante;
    private String motivo;

    private Integer idUsuario;
    private String nombreUsuario;

    private Integer idStock;
}
