package com.spectrayan.promptly.workflow.application.port.out;

import com.spectrayan.promptly.workflow.domain.model.Workflow;
import com.spectrayan.promptly.workflow.domain.model.WorkflowStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for workflow persistence.
 */
public interface WorkflowPersistencePort {

    Mono<Workflow> save(Workflow workflow);

    Mono<Workflow> findById(String id);

    Flux<Workflow> findByStatus(WorkflowStatus status);

    Flux<Workflow> findByPromptId(String promptId);

    Flux<Workflow> findByProjectId(String projectId);

    Flux<Workflow> findAll();

}
