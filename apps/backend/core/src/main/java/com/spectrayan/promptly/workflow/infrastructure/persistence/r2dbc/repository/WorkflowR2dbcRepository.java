package com.spectrayan.promptly.workflow.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.workflow.infrastructure.persistence.r2dbc.entity.WorkflowR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data R2DBC repository for the {@code workflows} table.
 */
public interface WorkflowR2dbcRepository extends R2dbcRepository<WorkflowR2dbcEntity, String> {

    Flux<WorkflowR2dbcEntity> findByStatus(String status);

    Flux<WorkflowR2dbcEntity> findByPromptId(String promptId);

    Flux<WorkflowR2dbcEntity> findByProjectId(String projectId);
}
