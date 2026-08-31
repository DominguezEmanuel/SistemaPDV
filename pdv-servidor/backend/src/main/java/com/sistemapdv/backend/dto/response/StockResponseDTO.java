package com.sistemapdv.backend.dto.response;


import com.sistemapdv.backend.utils.enums.EstadoStock;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {
    private Integer idStock;

    private Integer cantidadDisponible;
    private Integer stockMinimo;
    private EstadoStock estado;

    private Integer idProducto;
    private String nombreProducto;

    private Integer idVariante;
    private String nombreVariante;
    private String codigoBarras;
    private String codigoInterno;

    private Integer idCanalVenta;
    private String nombreCanalVenta;
}
