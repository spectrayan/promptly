package com.spectrayan.promptly.prompt.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.prompt.infrastructure.persistence.r2dbc.entity.PromptR2dbcEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code prompts} table.
 */
public interface PromptR2dbcRepository extends R2dbcRepository<PromptR2dbcEntity, String> {

    Flux<PromptR2dbcEntity> findByProjectId(String projectId);

    Flux<PromptR2dbcEntity> findByProjectId(String projectId, Pageable pageable);

    Flux<PromptR2dbcEntity> findAllBy(Pageable pageable);

    Mono<Boolean> existsByNameAndProjectId(String name, String projectId);
}
