package com.promptly.llmconfig.infrastructure.persistence.r2dbc.repository;

import com.promptly.llmconfig.infrastructure.persistence.r2dbc.entity.LlmConfigR2dbcEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code llm_configs} table.
 */
public interface LlmConfigR2dbcRepository extends R2dbcRepository<LlmConfigR2dbcEntity, String> {

    Mono<LlmConfigR2dbcEntity> findByProjectIdAndFeature(String projectId, String feature);

    Flux<LlmConfigR2dbcEntity> findByProjectId(String projectId);

    @Modifying
    @Query("DELETE FROM llm_configs WHERE project_id = :projectId AND feature = :feature")
    Mono<Void> deleteByProjectIdAndFeature(String projectId, String feature);
}
