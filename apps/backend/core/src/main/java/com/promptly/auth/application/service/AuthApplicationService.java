package com.promptly.auth.application.service;

import com.promptly.auth.application.port.in.AuthenticationUseCase;
import com.promptly.auth.application.port.in.UserQueryUseCase;
import com.promptly.auth.application.port.out.UserRepository;
import com.promptly.auth.domain.model.OrgRole;
import com.promptly.auth.domain.model.User;
import com.promptly.auth.domain.model.UserStatus;
import com.promptly.auth.infrastructure.security.JwtService;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Application service orchestrating auth use cases.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService implements AuthenticationUseCase, UserQueryUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Mono<AuthResult> register(RegisterCommand command) {
        log.info("Registering user: {}", command.email());

        return userRepository.existsByEmail(command.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already registered: " + command.email()));
                    }

                    // First user becomes ORG_ADMIN
                    return userRepository.count()
                            .flatMap(count -> {
                                User user = User.builder()
                                        .email(command.email())
                                        .displayName(command.displayName())
                                        .passwordHash(passwordEncoder.encode(command.password()))
                                        .orgRole(count == 0 ? OrgRole.ORG_ADMIN : OrgRole.ORG_USER)
                                        .status(UserStatus.ACTIVE)
                                        .createdAt(Instant.now())
                                        .updatedAt(Instant.now())
                                        .build();

                                return userRepository.save(user);
                            });
                })
                .flatMap(this::generateAuthResult);
    }

    @Override
    public Mono<AuthResult> login(String email, String password) {
        log.info("Login attempt for: {}", email);

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email or password")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        return Mono.error(new IllegalArgumentException("Invalid email or password"));
                    }
                    if (user.getStatus() != UserStatus.ACTIVE) {
                        return Mono.error(new IllegalArgumentException("Account is inactive"));
                    }

                    // Update last login
                    user.setLastLoginAt(Instant.now());
                    return userRepository.save(user);
                })
                .flatMap(this::generateAuthResult);
    }

    @Override
    public Mono<AuthResult> refresh(String refreshToken) {
        String userId = jwtService.extractUserId(refreshToken);
        if (userId == null || !jwtService.isTokenValid(refreshToken)) {
            return Mono.error(new IllegalArgumentException("Invalid refresh token"));
        }

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", userId)))
                .flatMap(this::generateAuthResult);
    }

    @Override
    public Mono<User> getCurrentUser(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", userId)));
    }

    @Override
    public Flux<User> searchUsers(String query, int limit) {
        return userRepository.searchByEmailOrName(query, limit);
    }

    private Mono<AuthResult> generateAuthResult(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        long expiresIn = jwtService.getAccessTokenExpirationMs() / 1000;

        return Mono.just(new AuthResult(accessToken, refreshToken, expiresIn, user));
    }
}
