package com.spectrayan.promptly.auth.infrastructure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for LOCAL auth mode.
 * Auth endpoints are public; all others require a valid JWT.
 * <p>
 * CORS is handled inside the security chain (via {@code .cors()}) so that
 * error responses (401, 403) also carry the correct CORS headers.
 */
@Configuration
@ConditionalOnProperty(name = "promptly.auth.provider", havingValue = "local", matchIfMissing = true)
public class LocalAuthSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtAuthenticationFilter jwtAuthFilter) {
        return http
                // Enable CORS inside security so 401/403 responses include CORS headers
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // SSE endpoint — auth via ?token= query param (EventSource API cannot send headers).
                        // JwtAuthenticationFilter already extracts the token from the query param.
                        .pathMatchers(HttpMethod.GET, "/api/v1/sse/**").authenticated()
                        // Actuator — only health and prometheus are public
                        .pathMatchers("/actuator/health", "/actuator/health/**", "/actuator/prometheus").permitAll()
                        // Swagger — open (disabled entirely in prod via springdoc.enabled=false)
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // All other actuator endpoints require auth
                        .pathMatchers("/actuator/**").authenticated()
                        // Everything else requires authentication
                        .anyExchange().authenticated()
                )
                .securityContextRepository(new JwtSecurityContextRepository(jwtAuthFilter))
                .build();
    }
}

