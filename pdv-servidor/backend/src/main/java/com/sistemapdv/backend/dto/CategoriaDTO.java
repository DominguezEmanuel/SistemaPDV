package com.sistemapdv.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTO {
    Integer idCategoria;
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    String nombre;
}
