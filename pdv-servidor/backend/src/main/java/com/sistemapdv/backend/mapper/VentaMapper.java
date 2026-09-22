package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.response.VentaResponseDTO;
import com.sistemapdv.backend.entity.Caja;
import com.sistemapdv.backend.entity.Usuario;
import com.sistemapdv.backend.entity.Venta;
import com.sistemapdv.backend.utils.FormatterDates;
import com.sistemapdv.backend.utils.enums.EstadoVenta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class VentaMapper {

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

    public VentaResponseDTO toResponseDTO(Venta venta){
        VentaResponseDTO responseDTO = VentaResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .fechaHora(FormatterDates.formatearFechaRegistro(venta.getFechaHora()))
                .idUsuario(venta.getUsuario().getIdUsuario())
                .username(venta.getUsuario().getUsername())
                .idCaja(venta.getCaja().getIdCaja())
                .subtotal(venta.getSubtotal())
                .descuento(venta.getDescuento())
                .total(venta.getTotal())
                .estado(venta.getEstado())
                .build();
        return responseDTO;
    }
}
