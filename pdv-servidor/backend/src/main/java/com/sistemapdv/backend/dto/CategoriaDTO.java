package com.sistemapdv.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTO {
    Integer idCategoria;
    String nombre;
    String descripcion;
}
