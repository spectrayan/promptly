package com.promptly.workflow.infrastructure.persistence.mongo.repository;

import com.promptly.workflow.infrastructure.persistence.mongo.entity.WorkflowStepDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive MongoDB repository for the {@code workflow_steps} collection.
 */
public interface WorkflowStepReactiveMongoRepository extends ReactiveMongoRepository<WorkflowStepDocument, String> {

    /**
     * Finds all steps for a workflow, ordered by step number ascending.
     */
    Flux<WorkflowStepDocument> findByWorkflowIdOrderByStepAsc(String workflowId);

    /**
     * Finds a specific step of a workflow.
     */
    Mono<WorkflowStepDocument> findByWorkflowIdAndStep(String workflowId, int step);

    /**
     * Deletes all steps for a workflow (cascade on workflow delete).
     */
    Mono<Void> deleteByWorkflowId(String workflowId);
}
