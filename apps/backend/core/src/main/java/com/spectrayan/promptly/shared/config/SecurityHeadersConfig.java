package com.spectrayan.promptly.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.server.WebFilter;

/**
 * Adds standard security headers to all HTTP responses.
 * Active only in prod/perf profiles — dev is left open for easier debugging.
 */
@Configuration
@Profile({"prod", "perf"})
public class SecurityHeadersConfig {

    @Bean
    public WebFilter securityHeadersFilter() {
        return (exchange, chain) -> {
            var headers = exchange.getResponse().getHeaders();
            headers.set("X-Content-Type-Options", "nosniff");
            headers.set("X-Frame-Options", "DENY");
            headers.set("Referrer-Policy", "strict-origin-when-cross-origin");
            headers.set("X-XSS-Protection", "0"); // Modern CSP replaces this; 0 disables legacy filter
            headers.set("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
            // HSTS should be set at the reverse proxy/LB level, but set here as a fallback
            headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            return chain.filter(exchange);
        };
    }
}
