package com.api.crud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Esto indica que esta clase es una fuente de configuración de Spring
public class CorsConfig {
    @Bean // Esto le dice a Spring que el método corsConfigurer() producirá un bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica esta configuración a TODAS las rutas (endpoints) de tu API
                        .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500") // Asegúrate de incluir AMBOS por si Live Server usa uno u otro
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP que tu frontend usará
                        .allowedHeaders("*") // Permite todos los encabezados (Auth, Content-Type, etc.)
                        .allowCredentials(true) // Importante si usas sesiones, cookies o tokens JWT en los encabezados
                        .maxAge(3600); // Sugerido para cachear la preflight request
            }
        };
    }
}