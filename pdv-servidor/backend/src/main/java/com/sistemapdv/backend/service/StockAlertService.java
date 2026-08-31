package com.sistemapdv.backend.service;

import com.sistemapdv.backend.entity.Stock;
import org.springframework.stereotype.Service;

@Service
public class StockAlertService {

    private final EmailService emailService;

    public StockAlertService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void verificarStock(Stock stock){

        if(stock.getCantidadDisponible() == 0){

            // Enviar alerta sin stock

        }else if(stock.getCantidadDisponible() <= stock.getStockMinimo()){

            // Enviar alerta de stock bajo
        }
    }
}
