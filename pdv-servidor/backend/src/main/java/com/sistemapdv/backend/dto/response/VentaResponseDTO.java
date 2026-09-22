package com.sistemapdv.backend.dto.response;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
import com.sistemapdv.backend.dto.PagoDTO;
import com.sistemapdv.backend.utils.enums.EstadoVenta;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaResponseDTO {

    private Integer idVenta;
    private String fechaHora;

    private Integer idUsuario;

    private Integer idCaja;

    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private EstadoVenta estado;

    private List<DetalleVentaDTO> detalles;

    private List<PagoDTO> pagos;
}
