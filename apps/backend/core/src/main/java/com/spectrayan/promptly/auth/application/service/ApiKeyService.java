package com.spectrayan.promptly.auth.application.service;

import com.spectrayan.promptly.auth.application.port.in.ManageApiKeysUseCase;
import com.spectrayan.promptly.auth.application.port.out.ApiKeyPersistencePort;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService implements ManageApiKeysUseCase {

    private static final String KEY_PREFIX = "prk_live_";
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ApiKeyPersistencePort apiKeyPersistencePort;

    @Override
    public Mono<GeneratedApiKey> createApiKey(String projectId, String name, Integer expiresInDays, String createdBy) {
        String rawSecret = generateRandomString(32);
        String fullRawKey = KEY_PREFIX + rawSecret;
        String visiblePrefix = fullRawKey.substring(0, 14) + "...";
        String keyHash = hashKey(fullRawKey);

        Instant now = Instant.now();
        Instant expiresAt = (expiresInDays != null && expiresInDays > 0)
                ? now.plus(Duration.ofDays(expiresInDays))
                : null;

        ApiKey apiKey = ApiKey.builder()
                .id(UUID.randomUUID().toString())
                .projectId(projectId)
                .name(name)
                .keyPrefix(visiblePrefix)
                .keyHash(keyHash)
                .roles(List.of("ROLE_API_KEY", "ROLE_DELIVERY"))
                .revoked(false)
                .expiresAt(expiresAt)
                .createdBy(createdBy)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return apiKeyPersistencePort.save(apiKey)
                .map(saved -> new GeneratedApiKey(saved, fullRawKey));
    }

    @Override
    public Flux<ApiKey> listApiKeys(String projectId) {
        return apiKeyPersistencePort.findByProjectId(projectId);
    }

    @Override
    public Mono<Void> revokeApiKey(String projectId, String keyId) {
        return apiKeyPersistencePort.findById(keyId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("ApiKey", keyId)))
                .flatMap(key -> {
                    if (!projectId.equals(key.getProjectId())) {
                        return Mono.error(new ResourceNotFoundException("ApiKey", keyId));
                    }
                    key.revoke();
                    return apiKeyPersistencePort.save(key);
                })
                .then();
    }

    @Override
    public Mono<ApiKey> authenticateApiKey(String rawKey) {
        if (rawKey == null || rawKey.isBlank() || !rawKey.startsWith("prk_")) {
            return Mono.empty();
        }

        String hash = hashKey(rawKey.trim());
        return apiKeyPersistencePort.findByKeyHash(hash)
                .filter(ApiKey::isValid)
                .flatMap(key -> {
                    key.recordUsage();
                    // Update lastUsedAt asynchronously in background
                    apiKeyPersistencePort.save(key)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(
                                    k -> log.debug("Updated lastUsedAt for key: {}", key.getId()),
                                    err -> log.warn("Failed to update lastUsedAt for key {}: {}", key.getId(), err.getMessage())
                            );
                    return Mono.just(key);
                });
    }

    public static String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(SECURE_RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
