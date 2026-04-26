package com.promptly.workflow.application.port.in;

import com.promptly.workflow.domain.model.Workflow;
import reactor.core.publisher.Mono;

/**
 * Approve the current step of a workflow.
 */
public interface ApproveWorkflowUseCase {

    record ApproveWorkflowCommand(String workflowId, String approvedBy, String comment) {}

    Mono<Workflow> approveWorkflow(ApproveWorkflowCommand command);

}
