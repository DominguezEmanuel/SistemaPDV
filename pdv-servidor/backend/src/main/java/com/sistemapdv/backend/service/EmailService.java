package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.SendEmailDTO;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.exception.EmailException;
import com.sistemapdv.backend.utils.enums.EstadoStock;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final Logger logger =
            LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(String to, String subject, String htmlBody){

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

        }catch (MailException | MessagingException e){
            logger.error("Error al enviar correo electrónico", e);

            throw new EmailException("No se pudo enviar el correo electrónico");
        }
    }

    public void sendStockAlert(
            String to,
            Stock stock) {

        try {

            String htmlBody = generarStockAlertHtml(stock);

            String subject;

            if (stock.getEstado() == EstadoStock.SIN_STOCK) {
                subject = "Producto sin stock";
            } else {
                subject = "Stock bajo";
            }

            sendEmail(to, subject, htmlBody);

        } catch (Exception e) {

            logger.error(
                    "Error al cargar la plantilla de alerta de stock",
                    e
            );

            throw new EmailException(
                    "No se pudo preparar el correo de alerta de stock",
                    e
            );
        }
    }

    private String generarStockAlertHtml(Stock stock) {

        Context context = new Context();

        context.setVariable(
                "producto",
                stock.getVarianteProducto()
                        .getProducto()
                        .getNombre()
        );

        context.setVariable(
                "variante",
                stock.getVarianteProducto()
                        .getNombre()
        );

        context.setVariable(
                "canalVenta",
                stock.getCanalVenta()
                        .getNombre()
        );

        context.setVariable(
                "stockActual",
                stock.getCantidadDisponible()
        );

        context.setVariable(
                "stockMinimo",
                stock.getStockMinimo()
        );

        /*context.setVariable(
                "estado",
                stock.getEstado()
        );*/

        return templateEngine.process(
                "email/stock-alert",
                context
        );
    }
}
