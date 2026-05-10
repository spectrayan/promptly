package com.spectrayan.promptly.workflow.application.port.in;

import com.spectrayan.promptly.workflow.domain.model.Workflow;
import reactor.core.publisher.Mono;

/**
 * Reject the current step of a workflow.
 */
public interface RejectWorkflowUseCase {

    record RejectWorkflowCommand(String workflowId, String rejectedBy, String reason) {}

    Mono<Workflow> rejectWorkflow(RejectWorkflowCommand command);

}
