package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.SendEmailDTO;
import com.sistemapdv.backend.exception.EmailException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final Logger logger =
            LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(SendEmailDTO requestEmail){
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(requestEmail.getTo());
            message.setSubject(requestEmail.getSubject());
            message.setText(requestEmail.getBody());

            mailSender.send(message);
        }catch (MailException e){

            logger.error("Error al enviar correo electrónico", e);

            throw new EmailException("No se pudo enviar el correo electrónico");
        }
    }
}
