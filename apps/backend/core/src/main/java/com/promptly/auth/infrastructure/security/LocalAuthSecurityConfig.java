package com.promptly.auth.infrastructure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for LOCAL auth mode.
 * Auth endpoints are public; all others require a valid JWT.
 * Active only in prod profile — in dev, SecurityConfig.devSecurityFilterChain permits all.
 */
@Configuration
@ConditionalOnProperty(name = "promptly.auth.provider", havingValue = "local", matchIfMissing = true)
@Profile("prod")
public class LocalAuthSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtAuthenticationFilter jwtAuthFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Actuator
                        .pathMatchers("/actuator/**").permitAll()
                        // Everything else requires authentication
                        .anyExchange().authenticated()
                )
                .securityContextRepository(new JwtSecurityContextRepository(jwtAuthFilter))
                .build();
    }
}
