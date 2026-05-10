package com.spectrayan.promptly.workflow.domain.model;

import com.spectrayan.promptly.shared.domain.Specification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link WorkflowSpecifications}.
 * <p>
 * Exercises each leaf specification and composite business rule directly.
 */
@DisplayName("WorkflowSpecifications")
class WorkflowSpecificationsTest {

    // ── Fixtures ─────────────────────────────────────────────────

    private static Workflow workflowWith(WorkflowStatus status, boolean hasPendingSteps) {
        List<WorkflowStep> steps = new ArrayList<>(List.of(
                WorkflowStep.builder().step(1).role("REVIEWER").assignedTo("bob")
                        .action(hasPendingSteps ? "PENDING" : "APPROVED").build(),
                WorkflowStep.builder().step(2).role("LEAD").assignedTo("carol")
                        .action(hasPendingSteps ? "PENDING" : "APPROVED").build()
        ));
        return Workflow.builder()
                .id("wf-1").projectId("proj-1").promptId("p-1").promptVersion(1)
                .type("approval").status(status).currentStep(0)
                .requestedBy("alice").steps(steps).build();
    }

    // ═══════════════════════════════════════════════════════════════
    // isPending
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isPending")
    class IsPendingTests {
        @Test void pendingSatisfied() {
            assertThat(WorkflowSpecifications.isPending().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, true))).isTrue();
        }
        @Test void inReviewNotSatisfied() {
            assertThat(WorkflowSpecifications.isPending().isSatisfiedBy(workflowWith(WorkflowStatus.IN_REVIEW, true))).isFalse();
        }
        @Test void unsatisfiedReasonContainsStatus() {
            Workflow wf = workflowWith(WorkflowStatus.IN_REVIEW, true);
            assertThat(WorkflowSpecifications.isPending().unsatisfiedReason(wf)).contains("IN_REVIEW");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // isActionable
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isActionable")
    class IsActionableTests {
        @Test void pendingIsActionable() {
            assertThat(WorkflowSpecifications.isActionable().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, true))).isTrue();
        }
        @Test void inReviewIsActionable() {
            assertThat(WorkflowSpecifications.isActionable().isSatisfiedBy(workflowWith(WorkflowStatus.IN_REVIEW, true))).isTrue();
        }
        @Test void approvedNotActionable() {
            assertThat(WorkflowSpecifications.isActionable().isSatisfiedBy(workflowWith(WorkflowStatus.APPROVED, false))).isFalse();
        }
        @Test void rejectedNotActionable() {
            assertThat(WorkflowSpecifications.isActionable().isSatisfiedBy(workflowWith(WorkflowStatus.REJECTED, false))).isFalse();
        }
        @Test void cancelledNotActionable() {
            assertThat(WorkflowSpecifications.isActionable().isSatisfiedBy(workflowWith(WorkflowStatus.CANCELLED, false))).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // isNotTerminal
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isNotTerminal")
    class IsNotTerminalTests {
        @Test void pendingIsNotTerminal() {
            assertThat(WorkflowSpecifications.isNotTerminal().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, true))).isTrue();
        }
        @Test void approvedIsTerminal() {
            assertThat(WorkflowSpecifications.isNotTerminal().isSatisfiedBy(workflowWith(WorkflowStatus.APPROVED, false))).isFalse();
        }
        @Test void rejectedIsTerminal() {
            assertThat(WorkflowSpecifications.isNotTerminal().isSatisfiedBy(workflowWith(WorkflowStatus.REJECTED, false))).isFalse();
        }
        @Test void cancelledIsTerminal() {
            assertThat(WorkflowSpecifications.isNotTerminal().isSatisfiedBy(workflowWith(WorkflowStatus.CANCELLED, false))).isFalse();
        }
        @Test void unsatisfiedReasonContainsTerminalState() {
            Workflow wf = workflowWith(WorkflowStatus.APPROVED, false);
            assertThat(WorkflowSpecifications.isNotTerminal().unsatisfiedReason(wf)).contains("terminal").contains("APPROVED");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // hasPendingSteps
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("hasPendingSteps")
    class HasPendingStepsTests {
        @Test void withPendingStepsSatisfied() {
            assertThat(WorkflowSpecifications.hasPendingSteps().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, true))).isTrue();
        }
        @Test void withoutPendingStepsNotSatisfied() {
            assertThat(WorkflowSpecifications.hasPendingSteps().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, false))).isFalse();
        }
        @Test void unsatisfiedReasonMentionsNoPending() {
            Workflow wf = workflowWith(WorkflowStatus.PENDING, false);
            assertThat(WorkflowSpecifications.hasPendingSteps().unsatisfiedReason(wf)).contains("no pending steps");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Composite business rules
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("composite business rules")
    class CompositeTests {

        @Test @DisplayName("PENDING with pending steps is approvable")
        void pendingApprovable() {
            assertThat(WorkflowSpecifications.isApprovable().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, true))).isTrue();
        }

        @Test @DisplayName("PENDING without pending steps is NOT approvable")
        void pendingNoStepsNotApprovable() {
            assertThat(WorkflowSpecifications.isApprovable().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, false))).isFalse();
        }

        @Test @DisplayName("APPROVED is NOT approvable")
        void approvedNotApprovable() {
            assertThat(WorkflowSpecifications.isApprovable().isSatisfiedBy(workflowWith(WorkflowStatus.APPROVED, false))).isFalse();
        }

        @Test @DisplayName("PENDING with pending steps is rejectable")
        void pendingRejectable() {
            assertThat(WorkflowSpecifications.isRejectable().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, true))).isTrue();
        }

        @Test @DisplayName("PENDING is cancellable")
        void pendingCancellable() {
            assertThat(WorkflowSpecifications.isCancellable().isSatisfiedBy(workflowWith(WorkflowStatus.PENDING, true))).isTrue();
        }

        @Test @DisplayName("APPROVED is NOT cancellable")
        void approvedNotCancellable() {
            assertThat(WorkflowSpecifications.isCancellable().isSatisfiedBy(workflowWith(WorkflowStatus.APPROVED, false))).isFalse();
        }
    }
}
