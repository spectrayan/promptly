package com.promptly.workflow;

import com.promptly.AbstractIntegrationTest;
import com.promptly.prompt.application.port.in.CreatePromptUseCase;
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
    private CreatePromptUseCase createPromptUseCase;

    @Autowired
    private SubmitReviewUseCase submitReviewUseCase;

    @Autowired
    private ApproveWorkflowUseCase approveWorkflowUseCase;

    @Autowired
    private RejectWorkflowUseCase rejectWorkflowUseCase;

    @Test
    void shouldSubmitForReview() {
        var promptCmd = new CreatePromptUseCase.CreatePromptCommand(
                "Workflow Test Prompt", "For workflow testing",
                "wf-project", "TEXT", "Content", "test-user"
        );

        StepVerifier.create(
                createPromptUseCase.createPrompt(promptCmd)
                        .flatMap(prompt -> {
                            var cmd = new SubmitReviewUseCase.SubmitReviewCommand(
                                    prompt.getId(), 1, "test-user", "STAGING"
                            );
                            return submitReviewUseCase.submitForReview(cmd);
                        })
        )
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.PENDING);
            assertThat(workflow.getSteps()).isNotEmpty();
        })
        .verifyComplete();
    }

    @Test
    void shouldApproveAllStepsAndCompleteWorkflow() {
        var promptCmd = new CreatePromptUseCase.CreatePromptCommand(
                "Approval Test Prompt", "For approval testing",
                "wf-project", "TEXT", "Content", "test-user"
        );

        StepVerifier.create(
                createPromptUseCase.createPrompt(promptCmd)
                        .flatMap(prompt -> {
                            var cmd = new SubmitReviewUseCase.SubmitReviewCommand(
                                    prompt.getId(), 1, "test-user", "STAGING"
                            );
                            return submitReviewUseCase.submitForReview(cmd);
                        })
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
        var promptCmd = new CreatePromptUseCase.CreatePromptCommand(
                "Rejection Test Prompt", "For rejection testing",
                "wf-project", "TEXT", "Content", "test-user"
        );

        StepVerifier.create(
                createPromptUseCase.createPrompt(promptCmd)
                        .flatMap(prompt -> {
                            var cmd = new SubmitReviewUseCase.SubmitReviewCommand(
                                    prompt.getId(), 1, "test-user", "STAGING"
                            );
                            return submitReviewUseCase.submitForReview(cmd);
                        })
                        .flatMap(wf -> rejectWorkflowUseCase.rejectWorkflow(wf.getId(), "reviewer", "Not ready"))
        )
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.REJECTED);
        })
        .verifyComplete();
    }

}
