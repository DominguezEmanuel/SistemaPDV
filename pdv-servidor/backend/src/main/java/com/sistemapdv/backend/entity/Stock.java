package com.sistemapdv.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "stock",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_variante", "id_canal_venta"})
        }
)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock")
    private Integer idStock;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_variante", nullable = false)
    private VarianteProducto varianteProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_canal_venta", nullable = false)
    private CanalVenta canalVenta;
}
