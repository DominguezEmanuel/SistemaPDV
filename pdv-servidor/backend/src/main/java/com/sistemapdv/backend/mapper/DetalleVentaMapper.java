package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
import com.sistemapdv.backend.entity.DetalleVenta;
import com.sistemapdv.backend.entity.VarianteProducto;
import com.sistemapdv.backend.entity.Venta;
import com.sistemapdv.backend.model.venta.DetalleVentaProcesado;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DetalleVentaMapper {

    public DetalleVenta toDetalleVenta(DetalleVentaProcesado detalle, Venta venta){
        DetalleVenta detalleVenta = DetalleVenta.builder()
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(
                        detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()))
                )
                .venta(venta)
                .variante(detalle.getVariante())
                .build();
        return detalleVenta;
    }

    public DetalleVentaDTO toResponseDTO(DetalleVenta detalle){
        DetalleVentaDTO dto = DetalleVentaDTO.builder()
                .idDetalleVenta(detalle.getIdDetalleVenta())
                .idVariante(detalle.getVariante().getIdVariante())
                .nombreVariante(detalle.getVariante().getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
        return dto;
    }
}
