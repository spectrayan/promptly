package com.promptly.prompt.infrastructure.persistence.repository;

import com.promptly.prompt.infrastructure.persistence.entity.PromptDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data reactive repository for PromptDocument.
 * This is an internal infrastructure concern — not exposed beyond the persistence layer.
 */
public interface PromptReactiveMongoRepository extends ReactiveMongoRepository<PromptDocument, String> {

    Flux<PromptDocument> findByProjectId(String projectId);

    Flux<PromptDocument> findByProjectId(String projectId, Pageable pageable);

    Flux<PromptDocument> findAllBy(Pageable pageable);

    Mono<Boolean> existsByNameAndProjectId(String name, String projectId);

}
