package com.spectrayan.promptly.workflow.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.workflow.infrastructure.persistence.r2dbc.entity.WorkflowStepR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code workflow_steps} table.
 */
public interface WorkflowStepR2dbcRepository extends R2dbcRepository<WorkflowStepR2dbcEntity, String> {

    Flux<WorkflowStepR2dbcEntity> findByWorkflowIdOrderByStepAsc(String workflowId);

    Mono<WorkflowStepR2dbcEntity> findByWorkflowIdAndStep(String workflowId, int step);

    Mono<Void> deleteByWorkflowId(String workflowId);
}
