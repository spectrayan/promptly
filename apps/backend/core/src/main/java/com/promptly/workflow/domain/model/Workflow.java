package com.promptly.workflow.domain.model;

import com.promptly.shared.domain.AggregateRoot;
import com.promptly.shared.domain.Specification;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root for the Workflow bounded context.
 * Models a multi-step approval state machine for prompt changes.
 * <p>
 * Business rules are enforced via {@link WorkflowSpecifications} (Specification pattern).
 */
@Getter
@Setter
@SuperBuilder
public class Workflow extends AggregateRoot {

    private String projectId;
    private String promptId;
    private int promptVersion;
    private String type; // "approval"
    private WorkflowStatus status;
    private int currentStep;
    private String requestedBy;

    @lombok.Builder.Default
    private List<WorkflowStep> steps = new ArrayList<>();

    /**
     * Approves all remaining pending steps and marks the workflow as APPROVED.
     * <p>
     * In the current MVP flow a single "Approve" action from the UI
     * is intended to complete the entire workflow.
     *
     * @throws IllegalStateException if the workflow is not in an approvable state
     */
    public void approveCurrentStep(String comment) {
        assertSatisfies(WorkflowSpecifications.isApprovable(), "approve workflow");

        steps.stream()
                .filter(WorkflowStep::isPending)
                .forEach(step -> step.approve(comment));

        currentStep = steps.size();
        status = WorkflowStatus.APPROVED;
    }

    /**
     * Rejects the current step and marks the workflow as rejected.
     *
     * @throws IllegalStateException if the workflow is not in a rejectable state
     */
    public void rejectCurrentStep(String comment) {
        assertSatisfies(WorkflowSpecifications.isRejectable(), "reject workflow");

        WorkflowStep step = getCurrentStepObj();
        step.reject(comment);
        status = WorkflowStatus.REJECTED;
    }

    /**
     * Cancels the workflow.
     *
     * @throws IllegalStateException if the workflow is already in a terminal state
     */
    public void cancel() {
        assertSatisfies(WorkflowSpecifications.isCancellable(), "cancel workflow");
        status = WorkflowStatus.CANCELLED;
    }

    // ── Query methods (read-only checks via specifications) ──────────

    public boolean isApprovable() {
        return WorkflowSpecifications.isApprovable().isSatisfiedBy(this);
    }

    public boolean isRejectable() {
        return WorkflowSpecifications.isRejectable().isSatisfiedBy(this);
    }

    public boolean isCancellable() {
        return WorkflowSpecifications.isCancellable().isSatisfiedBy(this);
    }

    // ── Internal ─────────────────────────────────────────────────────

    private WorkflowStep getCurrentStepObj() {
        return steps.stream()
                .filter(WorkflowStep::isPending)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No pending step found in workflow " + getId()));
    }

    private void assertSatisfies(Specification<Workflow> spec, String context) {
        if (!spec.isSatisfiedBy(this)) {
            throw new IllegalStateException(
                    context + ": " + spec.unsatisfiedReason(this));
        }
    }

}
