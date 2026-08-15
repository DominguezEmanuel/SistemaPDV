package com.sistemapdv.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    private MultipartFile imagen;

    @NotNull(message = "El precio minorista es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio minorista debe ser mayor a cero")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precioMinorista;

    @NotNull(message = "El precio mayorista es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio mayorista debe ser mayor a cero")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precioMayorista;

    @NotNull(message = "Debe indicar la cantidad mínima para precio mayorista")
    @Min(value = 2, message = "La cantidad mínima mayorista debe ser al menos 2")
    private Integer minimoMayorista;

    @NotNull(message = "Debe indicar si el producto tiene variantes o no")
    private Boolean tieneVariantes;

    @NotNull(message = "Debe seleccionar una categoría")
    private Integer idCategoria;

    // Solamente lo utilizarán aquellos productos que NO tengan variantes pero SÍ tienen código de barras
    // para ser consultados
    private String codigoBarras;
}
