package com.promptly.workflow;

import com.promptly.AbstractIntegrationTest;
import com.promptly.workflow.application.port.in.*;
import com.promptly.workflow.domain.model.WorkflowStatus;
import com.promptly.workflow.infrastructure.persistence.mongo.repository.WorkflowReactiveMongoRepository;
import com.promptly.workflow.infrastructure.persistence.mongo.repository.WorkflowStepReactiveMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Workflow Engine module.
 * Uses {@code ALL_DEPENDENCIES} to load the prompt and project modules
 * required by WorkflowController's cross-module queries.
 * Verifies submit→approve→complete and submit→reject lifecycles.
 * <p>
 * Steps are now persisted in the dedicated {@code workflow_steps} collection
 * and hydrated on read via {@code WorkflowStepPersistencePort}.
 */
@ApplicationModuleTest(mode = BootstrapMode.ALL_DEPENDENCIES)
class WorkflowModuleIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SubmitReviewUseCase submitReviewUseCase;

    @Autowired
    private ApproveWorkflowUseCase approveWorkflowUseCase;

    @Autowired
    private RejectWorkflowUseCase rejectWorkflowUseCase;

    @Autowired
    private GetWorkflowUseCase getWorkflowUseCase;

    @Autowired
    private WorkflowReactiveMongoRepository workflowRepository;

    @Autowired
    private WorkflowStepReactiveMongoRepository stepRepository;

    @BeforeEach
    void cleanUp() {
        stepRepository.deleteAll().block();
        workflowRepository.deleteAll().block();
    }

    @Test
    void shouldSubmitForReviewWithStepsInDedicatedCollection() {
        var cmd = new SubmitReviewUseCase.SubmitReviewCommand(
                "dummy-prompt-id", "test-project", 1, "test-user"
        );

        StepVerifier.create(submitReviewUseCase.submitForReview(cmd))
        .assertNext(workflow -> {
            assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.PENDING);
            assertThat(workflow.getSteps()).hasSize(2);
            assertThat(workflow.getSteps().get(0).getRole()).isEqualTo("reviewer");
            assertThat(workflow.getSteps().get(1).getRole()).isEqualTo("approver");
        })
        .verifyComplete();

        // Verify steps are in the dedicated collection (not embedded)
        StepVerifier.create(stepRepository.count())
                .assertNext(count -> assertThat(count).isEqualTo(2L))
                .verifyComplete();
    }

    @Test
    void shouldApproveWorkflowWithHydratedSteps() {
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

        // Verify steps were updated in the collection
        StepVerifier.create(stepRepository.findByWorkflowIdOrderByStepAsc(
                workflowRepository.findAll().blockFirst().getId()))
                .assertNext(step -> assertThat(step.getAction()).isEqualTo("APPROVED"))
                .assertNext(step -> assertThat(step.getAction()).isEqualTo("APPROVED"))
                .verifyComplete();
    }

    @Test
    void shouldRejectWorkflowWithHydratedSteps() {
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

    @Test
    void shouldGetWorkflowByIdWithStepsHydrated() {
        var submitCmd = new SubmitReviewUseCase.SubmitReviewCommand(
                "hydrate-prompt-id", "test-project", 1, "test-user"
        );

        var saved = submitReviewUseCase.submitForReview(submitCmd).block();
        assertThat(saved).isNotNull();

        StepVerifier.create(getWorkflowUseCase.getWorkflowById(saved.getId()))
                .assertNext(wf -> {
                    assertThat(wf.getSteps()).hasSize(2);
                    assertThat(wf.getSteps().get(0).isPending()).isTrue();
                })
                .verifyComplete();
    }
}
