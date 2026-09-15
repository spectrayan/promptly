package com.spectrayan.promptly.auth.application.port.out;

import com.spectrayan.promptly.auth.domain.model.ApiKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outgoing port for persisting and querying API keys.
 */
public interface ApiKeyPersistencePort {

    Mono<ApiKey> save(ApiKey apiKey);

    Mono<ApiKey> findById(String id);

    Mono<ApiKey> findByKeyHash(String keyHash);

    Flux<ApiKey> findByProjectId(String projectId);

    Mono<Boolean> existsByIdAndProjectId(String id, String projectId);
}
