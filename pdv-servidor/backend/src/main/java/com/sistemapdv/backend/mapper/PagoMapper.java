package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.PagoDTO;
import com.sistemapdv.backend.entity.Pago;
import com.sistemapdv.backend.entity.Venta;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public Pago toPago(PagoDTO dto, Venta venta){
        return Pago.builder()
                .medioPago(dto.getMedioPago())
                .importe(dto.getImporte())
                .venta(venta)
                .build();
    }
}
