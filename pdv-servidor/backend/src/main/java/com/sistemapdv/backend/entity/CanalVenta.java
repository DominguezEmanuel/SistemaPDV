package com.sistemapdv.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "canales_venta")
public class CanalVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_canal_venta")
    private Integer idCanalVenta;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;
}
