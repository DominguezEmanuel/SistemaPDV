package com.sistemapdv.backend.dto.request;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
import com.sistemapdv.backend.dto.PagoDTO;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {

    private Integer idCaja;

    private List<DetalleVentaDTO> detalles;

    private BigDecimal descuento;

    private List<PagoDTO> pagos;
}
