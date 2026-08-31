package com.sistemapdv.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailDTO {

    @Email(message = "Debe ingresar un correo electrónico válido")
    @NotBlank(message = "Debe ingresar el correo electrónico")
    private String to;

    @NotBlank(message = "Debe ingresar el tema del correo")
    private String subject;

    @NotBlank(message = "Debe ingresar el mensaje del correo")
    private String body;
}
