package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.StockAlertDTO;
import com.sistemapdv.backend.exception.EmailException;
import com.sistemapdv.backend.utils.enums.EstadoStock;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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

    /**
     * Envia la notificación de alerta de stock al propietario del sistema
     *
     * @param to Correo electrónico del propietario del sistema
     * @param subject Tema del correo electrónico
     * @param htmlBody Cuerpo html del correo
     */
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
            logger.error("Error al enviar correo electrónico a {}", to, e);

            throw new EmailException("No se pudo enviar el correo electrónico", e);
        }
    }

    /**
     * Construye el correo para ser enviado al propietario
     *
     * @param to Correo electrónico del propietario del sistema
     * @param stock Registro de stock usado para obtener ciertos datos
     */
    public void sendStockAlert(
            String to,
            StockAlertDTO stock) {

        try {

            String htmlBody = generarStockAlert(stock);

            String subject = stock.getEstado().equals(EstadoStock.SIN_STOCK) ? "Producto sin Stock" :
                    "Producto con Stock Bajo";

            sendEmail(to, subject, htmlBody);

        } catch (Exception e) {

            logger.error("Error al preparar alerta de stock", e);

            throw new EmailException("No se pudo preparar el correo de alerta de stock", e);
        }
    }

    private String generarStockAlert(StockAlertDTO stock) {

        String estado = stock.getEstado().equals(EstadoStock.SIN_STOCK) ? "Sin stock" : "Stock bajo";

        Context context = new Context();

        context.setVariable(
                "producto",
                stock.getProducto()
        );

        context.setVariable(
                "variante",
                stock.getVariante()
        );

        context.setVariable(
                "canalVenta",
                stock.getCanalVenta()
        );

        context.setVariable(
                "stockActual",
                stock.getStockActual()
        );

        context.setVariable(
                "stockMinimo",
                stock.getStockMinimo()
        );

        context.setVariable(
                "estado",
                estado
        );

        return templateEngine.process(
                "email/stock-alert",
                context
        );
    }
}
