package com.clinchain.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Autoriser toutes les origines (ou spécifiez votre IP)
        config.addAllowedOrigin("*");
        // Ou plus sécurisé :
        // config.addAllowedOrigin("http://192.168.11.101:*");

        // Autoriser toutes les méthodes HTTP
        config.addAllowedMethod("*");

        // Autoriser tous les headers
        config.addAllowedHeader("*");

        // Autoriser les credentials (important pour JWT)
        config.setAllowCredentials(true);

        // Si vous utilisez allowCredentials, ne pas utiliser "*" pour origin
        // Utilisez plutôt :
        config.setAllowedOriginPatterns(java.util.List.of("*"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}