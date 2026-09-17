package com.sistemapdv.backend.entity;

import com.sistemapdv.backend.utils.enums.TipoMovimientoCaja;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "movimientos_caja")
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_caja")
    private Integer idMovimientoCaja;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimientoCaja tipoMovimientoCaja;

    @Column(name = "importe", nullable = false)
    private BigDecimal importe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;
}
