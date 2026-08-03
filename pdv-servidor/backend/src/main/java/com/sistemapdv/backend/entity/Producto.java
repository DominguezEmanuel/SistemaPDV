package com.sistemapdv.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "imagen", nullable = false, length = 255)
    private String imagen;

    @Column(name = "precio_minorista", nullable = false)
    private BigDecimal precioMinorista;

    @Column(name = "precio_mayorista", nullable = false)
    private BigDecimal precioMayorista;

    @Column(name = "cantidad_minima_mayorista", nullable = false)
    private Integer minimoMayorista;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
}
