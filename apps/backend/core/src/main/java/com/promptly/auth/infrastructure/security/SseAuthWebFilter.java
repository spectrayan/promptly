package com.promptly.auth.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter that enforces JWT authentication on SSE endpoints.
 * <p>
 * SSE endpoints are marked as {@code permitAll()} in the SecurityFilterChain
 * because the {@code EventSource} browser API cannot send custom headers.
 * Instead, clients pass the JWT via query parameter: {@code ?token=<jwt>}.
 * <p>
 * This filter runs BEFORE the route handler and rejects SSE requests
 * that lack a valid token with 401 Unauthorized.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class SseAuthWebFilter implements WebFilter {

    private static final String SSE_PATH_PREFIX = "/api/v1/sse/";

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (!path.startsWith(SSE_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        return jwtAuthFilter.authenticate(exchange.getRequest())
                .flatMap(auth -> {
                    // Token is valid — continue to SSE handler
                    log.debug("SSE auth OK: userId={}, path={}", auth.getPrincipal(), path);
                    return chain.filter(exchange);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // No valid token — reject
                    log.warn("SSE connection rejected: no valid token for path={}", path);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }));
    }
}
