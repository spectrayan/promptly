package com.promptly.workflow.application.port.out;

import com.promptly.workflow.domain.model.Workflow;
import com.promptly.workflow.domain.model.WorkflowStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for workflow persistence.
 */
public interface WorkflowRepository {

    Mono<Workflow> save(Workflow workflow);

    Mono<Workflow> findById(String id);

    Flux<Workflow> findByStatus(WorkflowStatus status);

    Flux<Workflow> findByPromptId(String promptId);

    Flux<Workflow> findAll();

}
