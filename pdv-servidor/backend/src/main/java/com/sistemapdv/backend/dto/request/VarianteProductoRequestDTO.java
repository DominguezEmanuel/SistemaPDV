package com.sistemapdv.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VarianteProductoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;
    //@NotBlank(message = "El código de barras es obligatorio")
    @Size(max = 50, message = "El código de barras no puede superar los 50 caracteres")
    private String codigoBarras;
    @NotNull(message = "Debe seleccionar un producto")
    private Integer idProducto;
}
