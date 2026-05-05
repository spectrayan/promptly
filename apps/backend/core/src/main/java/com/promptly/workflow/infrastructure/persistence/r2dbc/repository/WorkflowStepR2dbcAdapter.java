package com.promptly.workflow.infrastructure.persistence.r2dbc.repository;

import com.promptly.workflow.application.port.out.WorkflowStepPersistencePort;
import com.promptly.workflow.domain.model.WorkflowStep;
import com.promptly.workflow.infrastructure.persistence.r2dbc.entity.WorkflowStepR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * R2DBC adapter implementing {@link WorkflowStepPersistencePort} for SQL databases
 * (PostgreSQL, H2, SQLite).
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class WorkflowStepR2dbcAdapter implements WorkflowStepPersistencePort {

    private final WorkflowStepR2dbcRepository repository;

    @Override
    public Mono<WorkflowStep> save(String workflowId, WorkflowStep step) {
        return repository.save(toEntity(workflowId, step)).map(this::toDomain);
    }

    @Override
    public Flux<WorkflowStep> saveAll(String workflowId, List<WorkflowStep> steps) {
        var entities = steps.stream().map(s -> toEntity(workflowId, s)).toList();
        return repository.saveAll(entities).map(this::toDomain);
    }

    @Override
    public Flux<WorkflowStep> findByWorkflowId(String workflowId) {
        return repository.findByWorkflowIdOrderByStepAsc(workflowId).map(this::toDomain);
    }

    @Override
    public Mono<WorkflowStep> findByWorkflowIdAndStep(String workflowId, int stepNumber) {
        return repository.findByWorkflowIdAndStep(workflowId, stepNumber).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByWorkflowId(String workflowId) {
        return repository.deleteByWorkflowId(workflowId);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private WorkflowStepR2dbcEntity toEntity(String workflowId, WorkflowStep step) {
        return WorkflowStepR2dbcEntity.builder()
                .workflowId(workflowId)
                .step(step.getStep())
                .role(step.getRole())
                .assignedTo(step.getAssignedTo())
                .action(step.getAction())
                .comment(step.getComment())
                .actedAt(step.getActedAt())
                .build();
    }

    private WorkflowStep toDomain(WorkflowStepR2dbcEntity entity) {
        return WorkflowStep.builder()
                .step(entity.getStep())
                .role(entity.getRole())
                .assignedTo(entity.getAssignedTo())
                .action(entity.getAction())
                .comment(entity.getComment())
                .actedAt(entity.getActedAt())
                .build();
    }
}
