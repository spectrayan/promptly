package com.spectrayan.promptly.auth.application.port.in;

import com.spectrayan.promptly.auth.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Inbound port for authentication operations (LOCAL mode).
 */
public interface AuthenticationUseCase {

    Mono<AuthResult> register(RegisterCommand command);

    Mono<AuthResult> login(String email, String password);

    Mono<AuthResult> refresh(String refreshToken);

    Mono<User> getCurrentUser(String userId);

    Mono<User> updateProfile(String userId, UpdateProfileCommand command);

    record RegisterCommand(String email, String password, String displayName) {}

    record UpdateProfileCommand(String displayName, String avatarUrl) {}

    record AuthResult(String accessToken, String refreshToken, long expiresIn, User user) {}
}
