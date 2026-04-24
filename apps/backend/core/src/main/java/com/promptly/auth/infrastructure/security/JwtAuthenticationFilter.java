package com.promptly.auth.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Extracts and validates JWT from the Authorization header.
 * Builds a Spring Security Authentication from the token claims.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter {

    private final JwtService jwtService;

    public Mono<Authentication> authenticate(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return Mono.empty();
        }

        String userId = jwtService.extractUserId(token);
        String email = jwtService.extractEmail(token);

        if (userId == null) {
            return Mono.empty();
        }

        // Create authentication with userId as principal
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var auth = new UsernamePasswordAuthenticationToken(userId, email, authorities);

        return Mono.just(auth);
    }
}
