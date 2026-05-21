package com.spectrayan.promptly.workflow.domain.model;

import com.spectrayan.promptly.shared.domain.Specification;

/**
 * Domain specifications encoding the business rules for workflow lifecycle management.
 * <p>
 * Rules:
 * <ul>
 *   <li>Only PENDING or IN_REVIEW workflows can be approved or rejected</li>
 *   <li>Only non-terminal workflows (PENDING, IN_REVIEW) can be cancelled</li>
 *   <li>APPROVED, REJECTED, and CANCELLED workflows are terminal — no further transitions</li>
 * </ul>
 */
public final class WorkflowSpecifications {

    private WorkflowSpecifications() {} // utility class

    // ═══════════════════════════════════════════════════════════════════
    // Status-based Specifications
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Workflow is in PENDING status.
     */
    public static Specification<Workflow> isPending() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Workflow workflow) {
                return WorkflowStatus.PENDING.equals(workflow.getStatus());
            }

            @Override
            public String unsatisfiedReason(Workflow workflow) {
                return "Workflow status is " + workflow.getStatus()
                        + ". Expected PENDING.";
            }
        };
    }

    /**
     * Workflow is in a reviewable state (PENDING or IN_REVIEW).
     */
    public static Specification<Workflow> isActionable() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Workflow workflow) {
                return workflow.getStatus() == WorkflowStatus.PENDING
                        || workflow.getStatus() == WorkflowStatus.IN_REVIEW;
            }

            @Override
            public String unsatisfiedReason(Workflow workflow) {
                return "Workflow status is " + workflow.getStatus()
                        + ". Only PENDING or IN_REVIEW workflows can be approved/rejected.";
            }
        };
    }

    /**
     * Workflow has not yet reached a terminal state.
     */
    public static Specification<Workflow> isNotTerminal() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Workflow workflow) {
                return workflow.getStatus() != WorkflowStatus.APPROVED
                        && workflow.getStatus() != WorkflowStatus.REJECTED
                        && workflow.getStatus() != WorkflowStatus.CANCELLED;
            }

            @Override
            public String unsatisfiedReason(Workflow workflow) {
                return "Workflow is already in terminal state: " + workflow.getStatus()
                        + ". No further transitions allowed.";
            }
        };
    }

    /**
     * Workflow has at least one pending step to act upon.
     */
    public static Specification<Workflow> hasPendingSteps() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Workflow workflow) {
                return workflow.getSteps() != null
                        && workflow.getSteps().stream().anyMatch(WorkflowStep::isPending);
            }

            @Override
            public String unsatisfiedReason(Workflow workflow) {
                return "Workflow has no pending steps remaining.";
            }
        };
    }

    // ═══════════════════════════════════════════════════════════════════
    // Composite Business Rule Specifications
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Workflow can be approved — must be actionable and have pending steps.
     */
    public static Specification<Workflow> isApprovable() {
        return isActionable().and(hasPendingSteps());
    }

    /**
     * Workflow can be rejected — must be actionable and have pending steps.
     */
    public static Specification<Workflow> isRejectable() {
        return isActionable().and(hasPendingSteps());
    }

    /**
     * Workflow can be cancelled — must not be in a terminal state.
     */
    public static Specification<Workflow> isCancellable() {
        return isNotTerminal();
    }
}
