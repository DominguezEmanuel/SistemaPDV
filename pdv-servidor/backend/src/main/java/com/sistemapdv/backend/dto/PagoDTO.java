package com.sistemapdv.backend.dto;

import com.sistemapdv.backend.utils.enums.MedioPago;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoDTO {
    private Integer idPago;
    private MedioPago medioPago;
    private BigDecimal importe;
}
