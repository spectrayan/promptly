package com.promptly.workflow.infrastructure.persistence.mongo.repository;

import com.promptly.workflow.application.port.out.WorkflowStepPersistencePort;
import com.promptly.workflow.domain.model.WorkflowStep;
import com.promptly.workflow.infrastructure.persistence.mongo.entity.WorkflowStepDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * MongoDB adapter implementing {@link WorkflowStepPersistencePort}.
 * <p>
 * Persists workflow steps as individual documents in the {@code workflow_steps}
 * collection, replacing the previous embedded-array approach.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class WorkflowStepMongoAdapter implements WorkflowStepPersistencePort {

    private final WorkflowStepReactiveMongoRepository repository;

    @Override
    public Mono<WorkflowStep> save(String workflowId, WorkflowStep step) {
        return repository.save(toDocument(workflowId, step))
                .map(this::toDomain);
    }

    @Override
    public Flux<WorkflowStep> saveAll(String workflowId, List<WorkflowStep> steps) {
        var documents = steps.stream()
                .map(step -> toDocument(workflowId, step))
                .toList();
        // Delete existing steps first to handle updates (steps don't carry document IDs)
        return repository.deleteByWorkflowId(workflowId)
                .thenMany(repository.saveAll(documents))
                .map(this::toDomain);
    }

    @Override
    public Flux<WorkflowStep> findByWorkflowId(String workflowId) {
        return repository.findByWorkflowIdOrderByStepAsc(workflowId)
                .map(this::toDomain);
    }

    @Override
    public Mono<WorkflowStep> findByWorkflowIdAndStep(String workflowId, int stepNumber) {
        return repository.findByWorkflowIdAndStep(workflowId, stepNumber)
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByWorkflowId(String workflowId) {
        return repository.deleteByWorkflowId(workflowId);
    }

    // ── Mapping ────────────────────────────────────────────────────

    private WorkflowStepDocument toDocument(String workflowId, WorkflowStep step) {
        return WorkflowStepDocument.builder()
                .workflowId(workflowId)
                .step(step.getStep())
                .role(step.getRole())
                .assignedTo(step.getAssignedTo())
                .action(step.getAction())
                .comment(step.getComment())
                .actedAt(step.getActedAt())
                .build();
    }

    private WorkflowStep toDomain(WorkflowStepDocument doc) {
        return WorkflowStep.builder()
                .step(doc.getStep())
                .role(doc.getRole())
                .assignedTo(doc.getAssignedTo())
                .action(doc.getAction())
                .comment(doc.getComment())
                .actedAt(doc.getActedAt())
                .build();
    }
}
