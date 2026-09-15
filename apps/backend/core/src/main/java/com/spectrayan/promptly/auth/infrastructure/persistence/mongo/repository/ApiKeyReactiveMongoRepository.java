package com.spectrayan.promptly.auth.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.auth.infrastructure.persistence.mongo.entity.ApiKeyDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive MongoDB repository for {@link ApiKeyDocument}.
 */
public interface ApiKeyReactiveMongoRepository extends ReactiveMongoRepository<ApiKeyDocument, String> {

    Mono<ApiKeyDocument> findByKeyHash(String keyHash);

    Flux<ApiKeyDocument> findByProjectId(String projectId);

    Mono<Boolean> existsByIdAndProjectId(String id, String projectId);
}
