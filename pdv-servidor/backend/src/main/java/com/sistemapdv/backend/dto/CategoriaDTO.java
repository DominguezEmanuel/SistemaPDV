package com.sistemapdv.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTO {
    @NotBlank
    Integer idCategoria;
    @NotBlank
    String nombre;
}
