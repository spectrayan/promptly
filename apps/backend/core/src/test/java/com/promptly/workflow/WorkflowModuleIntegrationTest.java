package com.promptly.workflow;

import com.promptly.AbstractIntegrationTest;
import com.promptly.prompt.application.port.in.CreatePromptUseCase;
import com.promptly.workflow.application.port.in.*;
import com.promptly.workflow.domain.model.Workflow;
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
                "dummy-prompt-id", 1, "test-user"
        );

        StepVerifier.create(submitReviewUseCase.submitForReview(cmd))
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.PENDING);
            assertThat(workflow.getSteps()).isNotEmpty();
        })
        .verifyComplete();
    }

    @Test
    void shouldApproveAllStepsAndCompleteWorkflow() {
        var cmd = new SubmitReviewUseCase.SubmitReviewCommand(
                "dummy-prompt-id", 1, "test-user"
        );

        StepVerifier.create(
                submitReviewUseCase.submitForReview(cmd)
                        .flatMap(wf -> approveWorkflowUseCase.approveWorkflow(wf.getId(), "reviewer", "LGTM"))
                        .flatMap(wf -> approveWorkflowUseCase.approveWorkflow(wf.getId(), "admin", "Approved"))
        )
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
        })
        .verifyComplete();
    }

    @Test
    void shouldRejectWorkflow() {
        var cmd = new SubmitReviewUseCase.SubmitReviewCommand(
                "dummy-prompt-id", 1, "test-user"
        );

        StepVerifier.create(
                submitReviewUseCase.submitForReview(cmd)
                        .flatMap(wf -> rejectWorkflowUseCase.rejectWorkflow(wf.getId(), "reviewer", "Not ready"))
        )
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.REJECTED);
        })
        .verifyComplete();
    }

}
