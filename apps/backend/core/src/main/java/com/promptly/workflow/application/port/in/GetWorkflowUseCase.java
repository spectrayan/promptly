package com.promptly.workflow.application.port.in;

import com.promptly.workflow.domain.model.Workflow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Retrieve workflows.
 */
public interface GetWorkflowUseCase {

    Mono<Workflow> getWorkflowById(String id);

    Flux<Workflow> getPendingWorkflows();

    Flux<Workflow> getWorkflowsByPromptId(String promptId);

}
