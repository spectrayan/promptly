package com.spectrayan.promptly.auth.application.service;

import com.spectrayan.promptly.auth.application.port.out.ApiKeyPersistencePort;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyPersistencePort apiKeyPersistencePort;

    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp() {
        apiKeyService = new ApiKeyService(apiKeyPersistencePort);
    }

    @Test
    @DisplayName("createApiKey should generate key with prefix prk_live_, SHA-256 hash, and return raw key")
    void createApiKey_shouldGenerateSecureRandomKeyWithPrefixAndHash() {
        when(apiKeyPersistencePort.save(any(ApiKey.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(apiKeyService.createApiKey("proj-1", "Prod Agent", 30, "user-123"))
                .assertNext(gen -> {
                    assertThat(gen.rawKey()).startsWith("prk_live_");
                    assertThat(gen.rawKey()).hasSize(41); // "prk_live_" (9) + 32 chars

                    ApiKey key = gen.apiKey();
                    assertThat(key.getId()).isNotNull();
                    assertThat(key.getProjectId()).isEqualTo("proj-1");
                    assertThat(key.getName()).isEqualTo("Prod Agent");
                    assertThat(key.getKeyPrefix()).startsWith("prk_live_");
                    assertThat(key.getKeyHash()).hasSize(64); // SHA-256 hex string
                    assertThat(key.getKeyHash()).isNotEqualTo(gen.rawKey());
                    assertThat(key.getKeyHash()).isEqualTo(ApiKeyService.hashKey(gen.rawKey()));
                    assertThat(key.getRoles()).contains("ROLE_API_KEY", "ROLE_DELIVERY");
                    assertThat(key.isRevoked()).isFalse();
                    assertThat(key.getExpiresAt()).isAfter(Instant.now());
                    assertThat(key.getCreatedBy()).isEqualTo("user-123");
                })
                .verifyComplete();

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyPersistencePort).save(captor.capture());
        assertThat(captor.getValue().getKeyHash()).isNotEqualTo(captor.getValue().getKeyPrefix());
    }

    @Test
    @DisplayName("listApiKeys should query persistence port by projectId")
    void listApiKeys_shouldReturnKeysByProjectId() {
        ApiKey key1 = ApiKey.builder().id("k1").projectId("proj-1").name("Key 1").build();
        ApiKey key2 = ApiKey.builder().id("k2").projectId("proj-1").name("Key 2").build();

        when(apiKeyPersistencePort.findByProjectId("proj-1"))
                .thenReturn(Flux.just(key1, key2));

        StepVerifier.create(apiKeyService.listApiKeys("proj-1"))
                .expectNext(key1, key2)
                .verifyComplete();
    }

    @Test
    @DisplayName("revokeApiKey should set revoked to true and update timestamp")
    void revokeApiKey_shouldMarkKeyAsRevoked() {
        ApiKey key = ApiKey.builder()
                .id("k1")
                .projectId("proj-1")
                .revoked(false)
                .build();

        when(apiKeyPersistencePort.findById("k1")).thenReturn(Mono.just(key));
        when(apiKeyPersistencePort.save(any(ApiKey.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(apiKeyService.revokeApiKey("proj-1", "k1"))
                .verifyComplete();

        assertThat(key.isRevoked()).isTrue();
        assertThat(key.getRevokedAt()).isNotNull();
        verify(apiKeyPersistencePort).save(key);
    }

    @Test
    @DisplayName("revokeApiKey should fail if projectId does not match")
    void revokeApiKey_shouldFailWhenProjectMismatches() {
        ApiKey key = ApiKey.builder()
                .id("k1")
                .projectId("proj-other")
                .revoked(false)
                .build();

        when(apiKeyPersistencePort.findById("k1")).thenReturn(Mono.just(key));

        StepVerifier.create(apiKeyService.revokeApiKey("proj-1", "k1"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(apiKeyPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("authenticateApiKey should authenticate valid key and record usage")
    void authenticateApiKey_shouldAuthenticateValidKeyAndUpdateUsage() {
        String rawKey = "prk_live_12345678901234567890123456789012";
        String hash = ApiKeyService.hashKey(rawKey);

        ApiKey key = ApiKey.builder()
                .id("k1")
                .projectId("proj-1")
                .keyHash(hash)
                .revoked(false)
                .expiresAt(Instant.now().plus(Duration.ofDays(10)))
                .build();

        when(apiKeyPersistencePort.findByKeyHash(hash)).thenReturn(Mono.just(key));
        when(apiKeyPersistencePort.save(any(ApiKey.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(apiKeyService.authenticateApiKey(rawKey))
                .assertNext(k -> {
                    assertThat(k.getId()).isEqualTo("k1");
                    assertThat(k.getLastUsedAt()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("authenticateApiKey should reject revoked key")
    void authenticateApiKey_shouldRejectRevokedKey() {
        String rawKey = "prk_live_revokedkey1234567890123456789012";
        String hash = ApiKeyService.hashKey(rawKey);

        ApiKey key = ApiKey.builder()
                .id("k1")
                .projectId("proj-1")
                .keyHash(hash)
                .revoked(true)
                .build();

        when(apiKeyPersistencePort.findByKeyHash(hash)).thenReturn(Mono.just(key));

        StepVerifier.create(apiKeyService.authenticateApiKey(rawKey))
                .verifyComplete(); // Empty Mono
    }

    @Test
    @DisplayName("authenticateApiKey should reject expired key")
    void authenticateApiKey_shouldRejectExpiredKey() {
        String rawKey = "prk_live_expiredkey1234567890123456789012";
        String hash = ApiKeyService.hashKey(rawKey);

        ApiKey key = ApiKey.builder()
                .id("k1")
                .projectId("proj-1")
                .keyHash(hash)
                .revoked(false)
                .expiresAt(Instant.now().minus(Duration.ofDays(1)))
                .build();

        when(apiKeyPersistencePort.findByKeyHash(hash)).thenReturn(Mono.just(key));

        StepVerifier.create(apiKeyService.authenticateApiKey(rawKey))
                .verifyComplete(); // Empty Mono
    }

    @Test
    @DisplayName("authenticateApiKey should return empty for null or non-prk key")
    void authenticateApiKey_shouldReturnEmptyForInvalidKeyString() {
        StepVerifier.create(apiKeyService.authenticateApiKey(null)).verifyComplete();
        StepVerifier.create(apiKeyService.authenticateApiKey("")).verifyComplete();
        StepVerifier.create(apiKeyService.authenticateApiKey("invalid-token-without-prefix")).verifyComplete();
    }
}
