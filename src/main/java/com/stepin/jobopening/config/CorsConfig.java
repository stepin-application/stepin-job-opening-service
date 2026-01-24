package com.stepin.jobopening.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);

        // Allow requests from frontend and other services
        config.setAllowedOriginPatterns(
            Arrays.asList(
                "http://localhost:3000", // Frontend Next.js
                "http://localhost:8082", // Campaign Service
                "http://localhost:8083", // Auth Service (future)
                "http://127.0.0.1:3000",
                "http://127.0.0.1:8082",
                "http://127.0.0.1:8083"
            )
        );

        // Allow all HTTP methods
        config.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Expose headers that the client can access
        config.setExposedHeaders(
            Arrays.asList(
                "Authorization",
                "Content-Type",
                "Content-Disposition"
            )
        );

        // How long the response from a pre-flight request can be cached (1 hour)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
