package com.promptly.auth.application.port.in;

import com.promptly.auth.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Inbound port for authentication operations (LOCAL mode).
 */
public interface AuthenticationUseCase {

    Mono<AuthResult> register(RegisterCommand command);

    Mono<AuthResult> login(String email, String password);

    Mono<AuthResult> refresh(String refreshToken);

    Mono<User> getCurrentUser(String userId);

    record RegisterCommand(String email, String password, String displayName) {}

    record AuthResult(String accessToken, String refreshToken, long expiresIn, User user) {}
}
