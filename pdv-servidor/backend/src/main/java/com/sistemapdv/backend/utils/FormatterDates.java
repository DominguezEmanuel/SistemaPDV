package com.sistemapdv.backend.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class FormatterDates {

    private FormatterDates(){
        throw new UnsupportedOperationException("Clase utilitaria, no debe ser instanciada");
    }

    public static String formatearFechaRegistro(OffsetDateTime fechaOriginal){

        if(fechaOriginal == null){
            return null;
        }

        OffsetDateTime fechaLocal = fechaOriginal
                .atZoneSameInstant(ZoneId.systemDefault())
                .toOffsetDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM - HH:mm");

        return fechaLocal.format(formatter);
    }
}
