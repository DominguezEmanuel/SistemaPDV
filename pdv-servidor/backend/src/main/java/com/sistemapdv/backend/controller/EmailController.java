package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.SendEmailDTO;
import com.sistemapdv.backend.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /*@PostMapping("/enviar")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody SendEmailDTO request){
        emailService.sendEmail(request);

        return ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente"));
    }*/

}
