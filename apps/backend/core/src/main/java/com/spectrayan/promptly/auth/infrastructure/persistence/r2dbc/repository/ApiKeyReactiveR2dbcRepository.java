package com.spectrayan.promptly.auth.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.auth.infrastructure.persistence.r2dbc.entity.ApiKeyR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for {@code api_keys}.
 */
public interface ApiKeyReactiveR2dbcRepository extends R2dbcRepository<ApiKeyR2dbcEntity, String> {

    Mono<ApiKeyR2dbcEntity> findByKeyHash(String keyHash);

    Flux<ApiKeyR2dbcEntity> findByProjectId(String projectId);

    Mono<Boolean> existsByIdAndProjectId(String id, String projectId);
}
