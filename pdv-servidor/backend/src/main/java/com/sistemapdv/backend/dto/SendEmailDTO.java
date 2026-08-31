package com.sistemapdv.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailDTO {

    @NotBlank(message = "Debe ingresar el correo electrónico")
    private String to;

    @NotBlank(message = "Debe ingresar el tema del correo")
    private String subject;

    @NotBlank(message = "Debe ingresar el mensaje del correo")
    private String body;
}
