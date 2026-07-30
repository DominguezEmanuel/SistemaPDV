package com.sistemapdv.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase encargada de configurar CORS (Cross-Origin Resource Sharing)
 * Habilita la comunicación entre el frontend Angular y el backend Spring Boot
 */
@Configuration
public class CorsConfig {

    /**
     * Crea un bean WebMvcConfigurer que configura globalmente las políticas CORS
     * Permite solicitudes desde orígenes externos autorizados
     */
    @Bean
    public WebMvcConfigurer corsConfigurer(){

        return new WebMvcConfigurer(){
            /**
             * Configura los mappings CORS para permitir que Angular se comunique con Spring Boot
             * 
             * NOTA: Para producción, reemplazar "http://localhost:4200" con la URL real del frontend
             * NOTA: Por seguridad, especificar solo los métodos y headers necesarios en producción
             */
            @Override
            public void addCorsMappings(CorsRegistry registry){

                // Aplica CORS a todas las rutas de la API
                registry.addMapping("/**")
                        // Solo permite solicitudes desde el frontend Angular (ambiente local)
                        // Cambiar a URL real en producción
                        .allowedOrigins("http://localhost:4200")
                        // Permite todos los métodos HTTP (GET, POST, PUT, DELETE, OPTIONS, etc.)
                        // En producción: .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedMethods("*")
                        // Permite todos los headers en las solicitudes
                        // En producción: .allowedHeaders("Content-Type", "Authorization")
                        .allowedHeaders("*");
            }
        };
    }
}
