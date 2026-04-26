package com.promptly.workflow;

import com.promptly.AbstractIntegrationTest;
import com.promptly.workflow.application.port.in.*;
import com.promptly.workflow.domain.model.WorkflowStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Workflow Engine module.
 * Verifies submit→approve→complete and submit→reject lifecycles.
 */
@ApplicationModuleTest
class WorkflowModuleIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SubmitReviewUseCase submitReviewUseCase;

    @Autowired
    private ApproveWorkflowUseCase approveWorkflowUseCase;

    @Autowired
    private RejectWorkflowUseCase rejectWorkflowUseCase;

    @Test
    void shouldSubmitForReview() {
        var cmd = new SubmitReviewUseCase.SubmitReviewCommand(
                "dummy-prompt-id", "test-project", 1, "test-user"
        );

        StepVerifier.create(submitReviewUseCase.submitForReview(cmd))
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.PENDING);
            assertThat(workflow.getSteps()).isNotEmpty();
        })
        .verifyComplete();
    }

    @Test
    void shouldApproveWorkflow() {
        var submitCmd = new SubmitReviewUseCase.SubmitReviewCommand(
                "dummy-prompt-id", "test-project", 1, "test-user"
        );

        StepVerifier.create(
                submitReviewUseCase.submitForReview(submitCmd)
                        .flatMap(wf -> approveWorkflowUseCase.approveWorkflow(
                                new ApproveWorkflowUseCase.ApproveWorkflowCommand(
                                        wf.getId(), "reviewer", "LGTM")))
        )
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
        })
        .verifyComplete();
    }

    @Test
    void shouldRejectWorkflow() {
        var submitCmd = new SubmitReviewUseCase.SubmitReviewCommand(
                "dummy-prompt-id", "test-project", 1, "test-user"
        );

        StepVerifier.create(
                submitReviewUseCase.submitForReview(submitCmd)
                        .flatMap(wf -> rejectWorkflowUseCase.rejectWorkflow(
                                new RejectWorkflowUseCase.RejectWorkflowCommand(
                                        wf.getId(), "reviewer", "Not ready")))
        )
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.REJECTED);
        })
        .verifyComplete();
    }

}
