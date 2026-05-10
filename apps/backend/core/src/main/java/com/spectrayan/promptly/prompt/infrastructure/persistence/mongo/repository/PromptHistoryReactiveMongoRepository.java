package com.spectrayan.promptly.prompt.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.prompt.infrastructure.persistence.mongo.entity.PromptHistoryDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive MongoDB repository for the {@code prompt_history} collection.
 */
public interface PromptHistoryReactiveMongoRepository extends ReactiveMongoRepository<PromptHistoryDocument, String> {

    /**
     * Finds all version history for a prompt, ordered by version number ascending.
     */
    Flux<PromptHistoryDocument> findByPromptIdOrderByVersionNumberAsc(String promptId);

    /**
     * Finds a specific version of a prompt.
     */
    Mono<PromptHistoryDocument> findByPromptIdAndVersionNumber(String promptId, int versionNumber);

    /**
     * Deletes all version history for a prompt (cascade on prompt delete).
     */
    Mono<Void> deleteByPromptId(String promptId);
}
