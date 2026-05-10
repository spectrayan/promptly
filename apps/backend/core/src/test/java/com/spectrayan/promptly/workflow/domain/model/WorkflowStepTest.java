package com.spectrayan.promptly.workflow.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link WorkflowStep} value object.
 */
@DisplayName("WorkflowStep")
class WorkflowStepTest {

    private static WorkflowStep pendingStep() {
        return WorkflowStep.builder()
                .step(1).role("REVIEWER").assignedTo("bob").action("PENDING")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // isPending
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isPending")
    class IsPendingTests {

        @Test @DisplayName("PENDING step is pending")
        void pendingIsPending() {
            assertThat(pendingStep().isPending()).isTrue();
        }

        @Test @DisplayName("APPROVED step is not pending")
        void approvedIsNotPending() {
            WorkflowStep step = pendingStep();
            step.approve("LGTM");
            assertThat(step.isPending()).isFalse();
        }

        @Test @DisplayName("REJECTED step is not pending")
        void rejectedIsNotPending() {
            WorkflowStep step = pendingStep();
            step.reject("No good");
            assertThat(step.isPending()).isFalse();
        }

        @Test @DisplayName("isPending is case-insensitive")
        void caseInsensitive() {
            WorkflowStep step = WorkflowStep.builder()
                    .step(1).role("R").assignedTo("x").action("pending")
                    .build();
            assertThat(step.isPending()).isTrue();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // approve
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("approve")
    class ApproveTests {

        @Test @DisplayName("should set action to APPROVED")
        void setsAction() {
            WorkflowStep step = pendingStep();
            step.approve("Looks good");
            assertThat(step.getAction()).isEqualTo("APPROVED");
        }

        @Test @DisplayName("should set comment")
        void setsComment() {
            WorkflowStep step = pendingStep();
            step.approve("Ship it");
            assertThat(step.getComment()).isEqualTo("Ship it");
        }

        @Test @DisplayName("should set actedAt timestamp")
        void setsTimestamp() {
            WorkflowStep step = pendingStep();
            step.approve("ok");
            assertThat(step.getActedAt()).isNotNull();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // reject
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("reject")
    class RejectTests {

        @Test @DisplayName("should set action to REJECTED")
        void setsAction() {
            WorkflowStep step = pendingStep();
            step.reject("Needs work");
            assertThat(step.getAction()).isEqualTo("REJECTED");
        }

        @Test @DisplayName("should set rejection comment")
        void setsComment() {
            WorkflowStep step = pendingStep();
            step.reject("Not aligned with policy");
            assertThat(step.getComment()).isEqualTo("Not aligned with policy");
        }

        @Test @DisplayName("should set actedAt timestamp")
        void setsTimestamp() {
            WorkflowStep step = pendingStep();
            step.reject("nope");
            assertThat(step.getActedAt()).isNotNull();
        }
    }
}
