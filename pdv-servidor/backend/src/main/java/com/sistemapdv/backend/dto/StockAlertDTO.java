package com.sistemapdv.backend.dto;

import com.sistemapdv.backend.utils.enums.EstadoStock;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockAlertDTO {
    private String producto;
    private String variante;
    private String canalVenta;
    private Integer stockActual;
    private Integer stockMinimo;
    private EstadoStock estado;
}
