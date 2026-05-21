package com.spectrayan.promptly.llmconfig.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.llmconfig.infrastructure.persistence.mongo.entity.LlmConfigDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LlmConfigReactiveMongoRepository extends ReactiveMongoRepository<LlmConfigDocument, String> {

    Mono<LlmConfigDocument> findByProjectIdAndFeature(String projectId, String feature);

    Flux<LlmConfigDocument> findByProjectId(String projectId);

    Mono<Void> deleteByProjectIdAndFeature(String projectId, String feature);
}
