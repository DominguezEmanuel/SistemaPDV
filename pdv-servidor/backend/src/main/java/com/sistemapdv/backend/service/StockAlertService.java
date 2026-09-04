package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.StockAlertDTO;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.exception.EmailException;
import com.sistemapdv.backend.utils.enums.EstadoStock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StockAlertService {

    private final EmailService emailService;

    @Value("${spring.mail.admin}")
    private String adminEmail;

    private static final Logger logger =
            LoggerFactory.getLogger(EmailService.class);

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

    /**
     * Verifica que el registro haya cambiado de estado y solicita a emailService
     * enviar una notificación de alerta de stock al propietario
     *
     * @param stock Registro de Stock
     */
    @Async
    public void procesarCambioEstado(StockAlertDTO stock){

        if (stock.getEstado() != EstadoStock.STOCK_BAJO &&
                stock.getEstado() != EstadoStock.SIN_STOCK) {
            return;
        }

        try{
            emailService.sendStockAlert(adminEmail, stock);
        }catch (EmailException e){
            logger.error(
                    "No se pudo enviar la alerta de stock para el producto {}",
                    stock.getProducto(), e
            );
        }
    }
}
