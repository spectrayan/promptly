package com.promptly.prompt.infrastructure.persistence.r2dbc.repository;

import com.promptly.prompt.infrastructure.persistence.r2dbc.entity.PromptHistoryR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code prompt_history} table.
 */
public interface PromptHistoryR2dbcRepository extends R2dbcRepository<PromptHistoryR2dbcEntity, String> {

    Flux<PromptHistoryR2dbcEntity> findByPromptIdOrderByVersionNumberAsc(String promptId);

    Mono<PromptHistoryR2dbcEntity> findByPromptIdAndVersionNumber(String promptId, int versionNumber);

    Mono<Void> deleteByPromptId(String promptId);
}
