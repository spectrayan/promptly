package com.promptly.workflow.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Workflow} aggregate root.
 * <p>
 * Tests the state machine transitions and Specification-based guard clauses
 * to ensure that only valid lifecycle transitions are permitted.
 */
@DisplayName("Workflow Aggregate")
class WorkflowTest {

    // ── Test Fixtures ────────────────────────────────────────────────

    private static Workflow pendingWorkflowWithSteps() {
        return Workflow.builder()
                .id("wf-1")
                .projectId("proj-1")
                .promptId("prompt-1")
                .promptVersion(1)
                .type("approval")
                .status(WorkflowStatus.PENDING)
                .currentStep(0)
                .requestedBy("alice@promptly.ai")
                .steps(new ArrayList<>(List.of(
                        WorkflowStep.builder().step(1).role("REVIEWER").assignedTo("bob").action("PENDING").build(),
                        WorkflowStep.builder().step(2).role("LEAD").assignedTo("carol").action("PENDING").build()
                )))
                .build();
    }

    private static Workflow inReviewWorkflowWithSteps() {
        Workflow wf = pendingWorkflowWithSteps();
        wf.setStatus(WorkflowStatus.IN_REVIEW);
        return wf;
    }

    private static Workflow approvedWorkflow() {
        Workflow wf = pendingWorkflowWithSteps();
        wf.setStatus(WorkflowStatus.APPROVED);
        wf.getSteps().forEach(s -> s.approve("LGTM"));
        return wf;
    }

    private static Workflow rejectedWorkflow() {
        Workflow wf = pendingWorkflowWithSteps();
        wf.setStatus(WorkflowStatus.REJECTED);
        return wf;
    }

    private static Workflow cancelledWorkflow() {
        Workflow wf = pendingWorkflowWithSteps();
        wf.setStatus(WorkflowStatus.CANCELLED);
        return wf;
    }

    // ═════════════════════════════════════════════════════════════════
    // Approve
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("approveCurrentStep")
    class ApproveTests {

        @Test
        @DisplayName("should approve all pending steps from PENDING status")
        void shouldApproveFromPending() {
            Workflow wf = pendingWorkflowWithSteps();

            wf.approveCurrentStep("LGTM");

            assertThat(wf.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
            assertThat(wf.getCurrentStep()).isEqualTo(2);
            assertThat(wf.getSteps())
                    .allSatisfy(step -> {
                        assertThat(step.getAction()).isEqualTo("APPROVED");
                        assertThat(step.getComment()).isEqualTo("LGTM");
                        assertThat(step.getActedAt()).isNotNull();
                    });
        }

        @Test
        @DisplayName("should approve from IN_REVIEW status")
        void shouldApproveFromInReview() {
            Workflow wf = inReviewWorkflowWithSteps();

            wf.approveCurrentStep("Approved in review");

            assertThat(wf.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
        }

        @Test
        @DisplayName("should throw when approving already APPROVED workflow")
        void shouldRejectApproveOnApproved() {
            Workflow wf = approvedWorkflow();

            assertThatThrownBy(() -> wf.approveCurrentStep("again"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("approve workflow");
        }

        @Test
        @DisplayName("should throw when approving REJECTED workflow")
        void shouldRejectApproveOnRejected() {
            Workflow wf = rejectedWorkflow();

            assertThatThrownBy(() -> wf.approveCurrentStep("try"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("approve workflow");
        }

        @Test
        @DisplayName("should throw when approving CANCELLED workflow")
        void shouldRejectApproveOnCancelled() {
            Workflow wf = cancelledWorkflow();

            assertThatThrownBy(() -> wf.approveCurrentStep("try"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("approve workflow");
        }

        @Test
        @DisplayName("should throw when workflow has no pending steps")
        void shouldRejectApproveWithNoPendingSteps() {
            Workflow wf = pendingWorkflowWithSteps();
            wf.getSteps().forEach(s -> s.approve("done"));

            assertThatThrownBy(() -> wf.approveCurrentStep("again"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no pending steps");
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // Reject
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("rejectCurrentStep")
    class RejectTests {

        @Test
        @DisplayName("should reject from PENDING status")
        void shouldRejectFromPending() {
            Workflow wf = pendingWorkflowWithSteps();

            wf.rejectCurrentStep("Needs rework");

            assertThat(wf.getStatus()).isEqualTo(WorkflowStatus.REJECTED);
            // Only the first pending step should be rejected
            assertThat(wf.getSteps().get(0).getAction()).isEqualTo("REJECTED");
            assertThat(wf.getSteps().get(0).getComment()).isEqualTo("Needs rework");
        }

        @Test
        @DisplayName("should reject from IN_REVIEW status")
        void shouldRejectFromInReview() {
            Workflow wf = inReviewWorkflowWithSteps();

            wf.rejectCurrentStep("Not ready");

            assertThat(wf.getStatus()).isEqualTo(WorkflowStatus.REJECTED);
        }

        @Test
        @DisplayName("should throw when rejecting terminal workflow")
        void shouldRejectOnTerminal() {
            Workflow wf = approvedWorkflow();

            assertThatThrownBy(() -> wf.rejectCurrentStep("too late"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("reject workflow");
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // Cancel
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("cancel")
    class CancelTests {

        @Test
        @DisplayName("should cancel from PENDING status")
        void shouldCancelFromPending() {
            Workflow wf = pendingWorkflowWithSteps();

            wf.cancel();

            assertThat(wf.getStatus()).isEqualTo(WorkflowStatus.CANCELLED);
        }

        @Test
        @DisplayName("should cancel from IN_REVIEW status")
        void shouldCancelFromInReview() {
            Workflow wf = inReviewWorkflowWithSteps();

            wf.cancel();

            assertThat(wf.getStatus()).isEqualTo(WorkflowStatus.CANCELLED);
        }

        @Test
        @DisplayName("should throw when cancelling APPROVED workflow")
        void shouldRejectCancelOnApproved() {
            Workflow wf = approvedWorkflow();

            assertThatThrownBy(wf::cancel)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("cancel workflow")
                    .hasMessageContaining("terminal");
        }

        @Test
        @DisplayName("should throw when cancelling REJECTED workflow")
        void shouldRejectCancelOnRejected() {
            Workflow wf = rejectedWorkflow();

            assertThatThrownBy(wf::cancel)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("terminal");
        }

        @Test
        @DisplayName("should throw when cancelling already CANCELLED workflow")
        void shouldRejectCancelOnCancelled() {
            Workflow wf = cancelledWorkflow();

            assertThatThrownBy(wf::cancel)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("terminal");
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // Query predicates
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("query predicates")
    class QueryTests {

        @Test
        @DisplayName("PENDING workflow with steps is approvable and rejectable")
        void pendingIsApprovableAndRejectable() {
            Workflow wf = pendingWorkflowWithSteps();

            assertThat(wf.isApprovable()).isTrue();
            assertThat(wf.isRejectable()).isTrue();
            assertThat(wf.isCancellable()).isTrue();
        }

        @Test
        @DisplayName("APPROVED workflow is not approvable, rejectable, or cancellable")
        void approvedIsTerminal() {
            Workflow wf = approvedWorkflow();

            assertThat(wf.isApprovable()).isFalse();
            assertThat(wf.isRejectable()).isFalse();
            assertThat(wf.isCancellable()).isFalse();
        }

        @Test
        @DisplayName("PENDING workflow with no pending steps is not approvable")
        void pendingNoPendingStepsIsNotApprovable() {
            Workflow wf = pendingWorkflowWithSteps();
            wf.getSteps().forEach(s -> s.approve("done"));

            assertThat(wf.isApprovable()).isFalse();
        }
    }
}
