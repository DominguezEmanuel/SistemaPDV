package com.sistemapdv.backend.dto;

import com.sistemapdv.backend.utils.enums.EstadoCaja;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CajaDTO {

    private Integer idCaja;

    private Integer idUsuario;
    private String username;

    private String fechaApertura;
    private String fechaCierre;

    @NotNull(message = "El monto inicial es obligatorio")
    @Min(message = "El monto inicial no puede ser menor a 0", value = 0)
    private BigDecimal montoInicial;

    private BigDecimal montoFinal;
    private BigDecimal saldoEsperado;
    private EstadoCaja estado;
}
