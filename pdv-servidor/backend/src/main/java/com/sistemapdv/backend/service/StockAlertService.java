package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.SendEmailDTO;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.utils.enums.EstadoStock;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StockAlertService {

    private final EmailService emailService;

    @Value("${spring.mail.admin}")
    private String adminEmail;

    public StockAlertService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void verificarStock(Stock stock){

        /*if(mismoEstado(stock)){
            return;
        }*/

        if(stock.getCantidadDisponible() == 0){

            // Enviar alerta sin stock
            //enviarAlertaSinStock(stock);

        }else if(stock.getCantidadDisponible() <= stock.getStockMinimo()){

            // Enviar alerta de stock bajo
            //enviarAlertaStockBajo(stock);

        }
    }

    public void procesarCambioEstado(Stock stock, EstadoStock estadoAnterior){
        EstadoStock estadoNuevo =
                stock.getEstado();

        if (estadoAnterior == estadoNuevo) {
            return;
        }

        if (estadoNuevo == EstadoStock.STOCK_BAJO ||
                estadoNuevo == EstadoStock.SIN_STOCK) {

            emailService.sendStockAlert(
                    adminEmail,
                    stock
            );
        }
    }

    /*
    public void sendStockAlert(
            String to,
            Stock stock,
            String tipoAlerta) {

        try {

            String html = cargarPlantillaStock();

            html = html.replace(
                    "{{producto}}",
                    stock.getVarianteProducto()
                            .getProducto()
                            .getNombre()
            );

            html = html.replace(
                    "{{variante}}",
                    stock.getVarianteProducto()
                            .getNombre()
            );

            html = html.replace(
                    "{{canalVenta}}",
                    stock.getCanalVenta()
                            .getNombre()
            );

            html = html.replace(
                    "{{stockActual}}",
                    String.valueOf(
                            stock.getCantidadDisponible()
                    )
            );

            html = html.replace(
                    "{{stockMinimo}}",
                    String.valueOf(
                            stock.getStockMinimo()
                    )
            );

            html = html.replace(
                    "{{tipoAlerta}}",
                    tipoAlerta
            );

            String subject;

            if (tipoAlerta.equals("AGOTADO")) {
                subject = "🔴 Producto sin stock";
            } else {
                subject = "⚠️ Stock bajo";
            }

            sendEmail(to, subject, html);

        } catch (IOException e) {

            logger.error(
                    "Error al cargar la plantilla de alerta de stock",
                    e
            );

            throw new EmailException(
                    "No se pudo preparar el correo de alerta de stock",
                    e
            );
        }
    }*/
}
