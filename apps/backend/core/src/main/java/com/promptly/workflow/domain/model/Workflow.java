package com.promptly.workflow.domain.model;

import com.promptly.shared.domain.AggregateRoot;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root for the Workflow bounded context.
 * Models a multi-step approval state machine for prompt changes.
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
     * Approves the current step and advances the workflow.
     * If all steps are approved, the workflow moves to APPROVED.
     */
    public void approveCurrentStep(String comment) {
        WorkflowStep step = getCurrentStepObj();
        step.approve(comment);

        if (currentStep < steps.size()) {
            currentStep++;
            // Check if this was the last step
            if (currentStep > steps.size()) {
                currentStep = steps.size();
            }
        }

        // If all steps are approved, mark workflow as approved
        boolean allApproved = steps.stream().noneMatch(WorkflowStep::isPending);
        if (allApproved) {
            status = WorkflowStatus.APPROVED;
        } else {
            status = WorkflowStatus.IN_REVIEW;
        }
    }

    /**
     * Rejects the current step and marks the workflow as rejected.
     */
    public void rejectCurrentStep(String comment) {
        WorkflowStep step = getCurrentStepObj();
        step.reject(comment);
        status = WorkflowStatus.REJECTED;
    }

    /**
     * Cancels the workflow.
     */
    public void cancel() {
        status = WorkflowStatus.CANCELLED;
    }

    private WorkflowStep getCurrentStepObj() {
        return steps.stream()
                .filter(WorkflowStep::isPending)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No pending step found in workflow " + getId()));
    }

}
