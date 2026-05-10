package com.spectrayan.promptly.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Global CORS configuration.
 * Origins are configured via {@code promptly.cors.allowed-origins}.
 * <p>
 * Exposes both a {@link CorsConfigurationSource} (consumed by Spring Security's
 * {@code .cors(Customizer.withDefaults())}) and a {@link CorsWebFilter} as a
 * fallback for non-security paths.
 * <p>
 * Dev default: http://localhost:4200,http://localhost:8080
 * Prod: set via CORS_ALLOWED_ORIGINS env var (e.g. https://app.promptly.dev)
 */
@Configuration
public class CorsConfig {

    private final PromptlyProperties.Cors corsProps;

    public CorsConfig(PromptlyProperties properties) {
        this.corsProps = properties.getCors();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = buildCorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsWebFilter corsFilter(CorsConfigurationSource corsConfigurationSource) {
        return new CorsWebFilter(corsConfigurationSource);
    }

    private CorsConfiguration buildCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = Arrays.stream(corsProps.getAllowedOrigins().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        config.setAllowCredentials(corsProps.isAllowCredentials());
        config.setMaxAge(3600L);
        return config;
    }
}
