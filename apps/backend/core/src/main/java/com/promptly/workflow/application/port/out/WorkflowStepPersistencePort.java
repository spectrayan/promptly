package com.promptly.workflow.application.port.out;

import com.promptly.workflow.domain.model.WorkflowStep;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for workflow step persistence.
 * <p>
 * Steps are stored separately from the workflow aggregate
 * (in a dedicated {@code workflow_steps} collection/table) to avoid
 * document bloat and enable SQL-compatible persistence.
 * <p>
 * Implemented by database-specific adapters:
 * <ul>
 *   <li>MongoDB: {@code WorkflowStepMongoAdapter}</li>
 *   <li>PostgreSQL: {@code WorkflowStepR2dbcAdapter} (future)</li>
 * </ul>
 */
public interface WorkflowStepPersistencePort {

    /**
     * Saves a single workflow step.
     *
     * @param workflowId the owning workflow's ID
     * @param step       the step to persist
     * @return the saved step
     */
    Mono<WorkflowStep> save(String workflowId, WorkflowStep step);

    /**
     * Saves all steps for a workflow (batch insert on creation).
     *
     * @param workflowId the owning workflow's ID
     * @param steps      the steps to persist
     * @return all saved steps
     */
    Flux<WorkflowStep> saveAll(String workflowId, java.util.List<WorkflowStep> steps);

    /**
     * Retrieves all steps for a given workflow, ordered by step number ascending.
     *
     * @param workflowId the owning workflow's ID
     * @return all steps in order
     */
    Flux<WorkflowStep> findByWorkflowId(String workflowId);

    /**
     * Retrieves a specific step of a workflow.
     *
     * @param workflowId the owning workflow's ID
     * @param stepNumber the step number to retrieve
     * @return the matching step, or empty if not found
     */
    Mono<WorkflowStep> findByWorkflowIdAndStep(String workflowId, int stepNumber);

    /**
     * Deletes all steps for a given workflow (cascade on workflow delete).
     *
     * @param workflowId the owning workflow's ID
     * @return completion signal
     */
    Mono<Void> deleteByWorkflowId(String workflowId);
}
