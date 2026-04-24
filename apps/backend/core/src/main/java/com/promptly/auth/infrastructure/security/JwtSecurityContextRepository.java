package com.promptly.auth.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Stores/loads the JWT-based SecurityContext for each request.
 */
@RequiredArgsConstructor
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        // Stateless — no saving
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return jwtAuthFilter.authenticate(exchange.getRequest())
                .map(SecurityContextImpl::new);
    }
}
