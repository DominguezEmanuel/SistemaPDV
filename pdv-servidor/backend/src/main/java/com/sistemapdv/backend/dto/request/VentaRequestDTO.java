package com.sistemapdv.backend.dto.request;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
import com.sistemapdv.backend.dto.PagoDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {

    @NotNull(message = "La caja es obligatoria")
    private Integer idCaja;

    @NotNull(message = "El canal de venta es obligatorio")
    private Integer idCanalVenta;

    private BigDecimal descuento;

    private List<DetalleVentaDTO> detalles;

    private List<PagoDTO> pagos;
}
