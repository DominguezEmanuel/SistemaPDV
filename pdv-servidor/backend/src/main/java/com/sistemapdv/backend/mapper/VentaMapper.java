package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.response.VentaResponseDTO;
import com.sistemapdv.backend.entity.Venta;
import com.sistemapdv.backend.utils.FormatterDates;
import org.springframework.stereotype.Component;

@Component
public class VentaMapper {

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
