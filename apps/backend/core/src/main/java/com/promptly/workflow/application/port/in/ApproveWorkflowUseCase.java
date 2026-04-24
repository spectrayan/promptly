package com.promptly.workflow.application.port.in;

import com.promptly.workflow.domain.model.Workflow;
import reactor.core.publisher.Mono;

/**
 * Approve the current step of a workflow.
 */
public interface ApproveWorkflowUseCase {

    Mono<Workflow> approveWorkflow(String workflowId, String approvedBy, String comment);

}
