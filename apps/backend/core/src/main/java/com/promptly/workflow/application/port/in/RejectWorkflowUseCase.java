package com.promptly.workflow.application.port.in;

import com.promptly.workflow.domain.model.Workflow;
import reactor.core.publisher.Mono;

/**
 * Reject the current step of a workflow.
 */
public interface RejectWorkflowUseCase {

    Mono<Workflow> rejectWorkflow(String workflowId, String rejectedBy, String reason);

}
