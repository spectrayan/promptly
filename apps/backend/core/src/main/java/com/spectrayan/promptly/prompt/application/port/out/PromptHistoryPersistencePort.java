package com.spectrayan.promptly.prompt.application.port.out;

import com.spectrayan.promptly.prompt.domain.model.PromptVersion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for prompt version history persistence.
 * <p>
 * Versions are stored separately from the prompt aggregate
 * (in a dedicated {@code prompt_history} collection/table) to avoid
 * document bloat and enable on-demand loading.
 * <p>
 * Implemented by database-specific adapters:
 * <ul>
 *   <li>MongoDB: {@code PromptHistoryMongoAdapter}</li>
 *   <li>PostgreSQL: {@code PromptHistoryR2dbcAdapter} (future)</li>
 * </ul>
 */
public interface PromptHistoryPersistencePort {

    /**
     * Saves a single prompt version to the history store.
     *
     * @param promptId the owning prompt's ID
     * @param version  the version to persist
     * @return the saved version
     */
    Mono<PromptVersion> save(String promptId, PromptVersion version);

    /**
     * Retrieves all versions for a given prompt, ordered by version number ascending.
     *
     * @param promptId the owning prompt's ID
     * @return all versions in chronological order
     */
    Flux<PromptVersion> findByPromptId(String promptId);

    /**
     * Retrieves a specific version of a prompt.
     *
     * @param promptId      the owning prompt's ID
     * @param versionNumber the version number to retrieve
     * @return the matching version, or empty if not found
     */
    Mono<PromptVersion> findByPromptIdAndVersion(String promptId, int versionNumber);

    /**
     * Deletes all version history for a given prompt (cascade on prompt delete).
     *
     * @param promptId the owning prompt's ID
     * @return completion signal
     */
    Mono<Void> deleteByPromptId(String promptId);
}
