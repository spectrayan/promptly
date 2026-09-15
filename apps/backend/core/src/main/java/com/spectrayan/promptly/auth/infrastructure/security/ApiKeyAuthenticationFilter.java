package com.spectrayan.promptly.auth.infrastructure.security;

import com.spectrayan.promptly.auth.application.port.in.ManageApiKeysUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Extracts and validates API keys from request headers.
 * Supports:
 * 1. {@code X-API-Key: prk_live_...}
 * 2. {@code Authorization: Bearer prk_live_...}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter {

    public static final String X_API_KEY_HEADER = "X-API-Key";

    private final ManageApiKeysUseCase manageApiKeysUseCase;

    public Mono<Authentication> authenticate(ServerHttpRequest request) {
        String apiKey = null;

        // 1. Try X-API-Key header
        String customHeader = request.getHeaders().getFirst(X_API_KEY_HEADER);
        if (customHeader != null && !customHeader.isBlank()) {
            apiKey = customHeader.trim();
        }

        // 2. Fallback to Authorization: Bearer prk_...
        if (apiKey == null) {
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer prk_")) {
                apiKey = authHeader.substring(7).trim();
            }
        }

        if (apiKey == null || !apiKey.startsWith("prk_")) {
            return Mono.empty();
        }

        return manageApiKeysUseCase.authenticateApiKey(apiKey)
                .map(key -> {
                    var authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_API_KEY"),
                            new SimpleGrantedAuthority("ROLE_USER")
                    );
                    // Principal is the apiKeyId; credentials is the projectId
                    return new UsernamePasswordAuthenticationToken(key.getId(), key.getProjectId(), authorities);
                });
    }
}
