package com.promptly.auth.infrastructure.web;

import com.promptly.auth.application.port.in.AuthenticationUseCase;
import com.promptly.auth.application.port.in.UserQueryUseCase;
import com.promptly.auth.domain.model.User;
import com.promptly.infrastructure.in.web.api.AuthApi;
import com.promptly.infrastructure.in.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST controller for auth endpoints.
 * Implements the contract-first AuthApi interface.
 */
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthenticationUseCase authUseCase;
    private final UserQueryUseCase userQueryUseCase;

    @Override
    public Mono<ResponseEntity<AuthResponse>> register(
            Mono<RegisterRequest> registerRequest, ServerWebExchange exchange) {
        return registerRequest
                .flatMap(req -> authUseCase.register(
                        new AuthenticationUseCase.RegisterCommand(
                                req.getEmail(), req.getPassword(), req.getDisplayName()
                        )))
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(toAuthResponse(result)));
    }

    @Override
    public Mono<ResponseEntity<AuthResponse>> login(
            Mono<LoginRequest> loginRequest, ServerWebExchange exchange) {
        return loginRequest
                .flatMap(req -> authUseCase.login(req.getEmail(), req.getPassword()))
                .map(result -> ResponseEntity.ok(toAuthResponse(result)));
    }

    @Override
    public Mono<ResponseEntity<AuthResponse>> refreshToken(
            Mono<RefreshTokenRequest> refreshTokenRequest, ServerWebExchange exchange) {
        return refreshTokenRequest
                .flatMap(req -> authUseCase.refresh(req.getRefreshToken()))
                .map(result -> ResponseEntity.ok(toAuthResponse(result)));
    }

    @Override
    public Mono<ResponseEntity<UserResponse>> getCurrentUser(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (String) ctx.getAuthentication().getPrincipal())
                .flatMap(authUseCase::getCurrentUser)
                .map(user -> ResponseEntity.ok(toUserResponse(user)));
    }

    @Override
    public Mono<ResponseEntity<Flux<UserResponse>>> listUsers(
            String q, Integer limit, ServerWebExchange exchange) {
        int maxResults = limit != null ? limit : 20;
        Flux<UserResponse> users = userQueryUseCase.searchUsers(q, maxResults)
                .map(this::toUserResponse);
        return Mono.just(ResponseEntity.ok(users));
    }

    /**
     * PATCH /api/v1/auth/me — Update the current user's profile (display name, avatar).
     * Not yet code-generated; manually added to match the OpenAPI spec update.
     */
    @PatchMapping("/api/v1/auth/me")
    public Mono<ResponseEntity<UserResponse>> updateCurrentUser(@RequestBody Map<String, String> body,
                                                                 ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (String) ctx.getAuthentication().getPrincipal())
                .flatMap(userId -> {
                    var command = new AuthenticationUseCase.UpdateProfileCommand(
                            body.get("displayName"),
                            body.get("avatarUrl")
                    );
                    return authUseCase.updateProfile(userId, command);
                })
                .map(user -> ResponseEntity.ok(toUserResponse(user)));
    }

    // ── Mapping ──

    private AuthResponse toAuthResponse(AuthenticationUseCase.AuthResult result) {
        var response = new AuthResponse();
        response.setAccessToken(result.accessToken());
        response.setRefreshToken(result.refreshToken());
        response.setExpiresIn((int) result.expiresIn());
        response.setUser(toUserResponse(result.user()));
        return response;
    }

    private UserResponse toUserResponse(User user) {
        var response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setDisplayName(user.getDisplayName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setOrgRole(OrgRole.fromValue(user.getOrgRole().name()));
        response.setStatus(UserStatus.fromValue(user.getStatus().name()));
        if (user.getCreatedAt() != null) {
            response.setCreatedAt(java.time.OffsetDateTime.ofInstant(user.getCreatedAt(), java.time.ZoneOffset.UTC));
        }
        return response;
    }
}
