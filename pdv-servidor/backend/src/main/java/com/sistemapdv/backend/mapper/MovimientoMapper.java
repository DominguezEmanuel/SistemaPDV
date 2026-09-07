package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.request.MovimientoRequestDTO;
import com.sistemapdv.backend.dto.response.MovimientoResponseDTO;
import com.sistemapdv.backend.entity.MovimientoStock;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.entity.Usuario;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.lang.Math.abs;

@Component
public class MovimientoMapper {

    public MovimientoStock toMovimiento(MovimientoRequestDTO request, Usuario usuario, Stock stock,
                                         int stockAnterior, int cantidadMovimiento){
        MovimientoStock movimiento = MovimientoStock.builder()
                .fechaHora(OffsetDateTime.now())
                .tipoMovimiento(request.getTipo())
                .cantidad(abs(cantidadMovimiento))
                .stockAnterior(stockAnterior)
                .stockResultante(stock.getCantidadDisponible())
                .motivo(request.getMotivo())
                .usuario(usuario)
                .stock(stock)
                .build();
        return movimiento;
    }

    public MovimientoResponseDTO toResponseDTO(MovimientoStock movimientoStock){
        MovimientoResponseDTO dto = MovimientoResponseDTO.builder()
                .idMovimiento(movimientoStock.getIdMovimiento())
                .fechaHora(movimientoStock.getFechaHora().toString())
                .tipo(movimientoStock.getTipoMovimiento())
                .cantidad(movimientoStock.getCantidad())
                .stockAnterior(movimientoStock.getStockAnterior())
                .stockResultante(movimientoStock.getStockResultante())
                .motivo(movimientoStock.getMotivo())
                .idUsuario(movimientoStock.getUsuario().getIdUsuario())
                .nombreUsuario(movimientoStock.getUsuario().getUsername())
                .idStock(movimientoStock.getStock().getIdStock())
                .build();

        return dto;
    }
}
