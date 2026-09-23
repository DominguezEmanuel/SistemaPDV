package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
import com.sistemapdv.backend.dto.response.VentaResponseDTO;
import com.sistemapdv.backend.entity.Caja;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.entity.Venta;
import com.sistemapdv.backend.utils.FormatterDates;
import com.sistemapdv.backend.utils.enums.EstadoVenta;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@Component
public class VentaMapper {

    private final DetalleVentaMapper detalleVentaMapper;

    public Venta toVenta(Caja caja, Usuario usuario, BigDecimal subtotal, BigDecimal descuento){
        Venta venta = Venta.builder()
                .fechaHora(OffsetDateTime.now())
                .subtotal(subtotal)
                .descuento(descuento)
                .total(subtotal.subtract(descuento))
                .estado(EstadoVenta.COMPLETADA)
                .usuario(usuario)
                .caja(caja)
                .build();
        return venta;
    }

    public VentaResponseDTO toResponseDTO(Venta venta,
                                          List<DetalleVentaDTO> detalles){
        VentaResponseDTO responseDTO = VentaResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .fechaHora(FormatterDates.formatearFechaRegistro(venta.getFechaHora()))
                .idUsuario(venta.getUsuario().getIdUsuario())
                .idCaja(venta.getCaja().getIdCaja())
                .subtotal(venta.getSubtotal())
                .descuento(venta.getDescuento())
                .total(venta.getTotal())
                .estado(venta.getEstado())
                .detalles(detalles)
                .build();

        return responseDTO;
    }
}
