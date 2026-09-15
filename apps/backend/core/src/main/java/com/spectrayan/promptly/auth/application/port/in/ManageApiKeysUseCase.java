package com.spectrayan.promptly.auth.application.port.in;

import com.spectrayan.promptly.auth.domain.model.ApiKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Incoming use case port for managing and authenticating project API keys.
 */
public interface ManageApiKeysUseCase {

    /**
     * DTO containing the newly created ApiKey entity along with its plaintext raw key.
     * The raw key is exposed ONLY once at generation time and never persisted.
     */
    record GeneratedApiKey(ApiKey apiKey, String rawKey) {}

    /**
     * Generates a new API key for a project.
     *
     * @param projectId     the target project
     * @param name          human-readable identifier
     * @param expiresInDays optional duration in days before key expires
     * @param createdBy     the user creating the key
     * @return generated key details containing the raw key
     */
    Mono<GeneratedApiKey> createApiKey(String projectId, String name, Integer expiresInDays, String createdBy);

    /**
     * Lists all API keys for a given project.
     *
     * @param projectId target project
     * @return flux of API keys
     */
    Flux<ApiKey> listApiKeys(String projectId);

    /**
     * Revokes an API key.
     *
     * @param projectId target project
     * @param keyId     key identifier
     * @return completion signal
     */
    Mono<Void> revokeApiKey(String projectId, String keyId);

    /**
     * Authenticates an incoming raw API key string.
     * Validates expiration, revocation, and updates usage timestamp asynchronously.
     *
     * @param rawKey the plaintext API key (e.g., "prk_live_...")
     * @return valid ApiKey entity, or empty Mono if invalid/revoked/expired
     */
    Mono<ApiKey> authenticateApiKey(String rawKey);
}
