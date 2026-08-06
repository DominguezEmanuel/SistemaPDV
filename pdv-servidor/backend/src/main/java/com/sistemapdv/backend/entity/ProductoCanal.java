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
        name = "productos_canales",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "id_producto",
                        "id_canal_venta"
                })
        }
)
public class ProductoCanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_canal")
    private Integer idProductoCanal;

    @Column(name = "limite_mayorista", nullable = false)
    private Integer limiteMayorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_canal_venta", nullable = false)
    private CanalVenta canalVenta;
}
