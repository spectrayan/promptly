package com.promptly.prompt.application.port.out;

import com.promptly.prompt.domain.model.Prompt;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for prompt persistence.
 * Implemented by PromptMongoAdapter in the infrastructure layer.
 * <p>
 * Returns domain models — never MongoDB documents.
 */
public interface PromptPersistencePort {

    Mono<Prompt> save(Prompt prompt);

    Mono<Prompt> findById(String id);

    Flux<Prompt> findByProjectId(String projectId);

    Flux<Prompt> findByProjectId(String projectId, Pageable pageable);

    Flux<Prompt> findAll();

    Flux<Prompt> findAll(Pageable pageable);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsByNameAndProjectId(String name, String projectId);

}
