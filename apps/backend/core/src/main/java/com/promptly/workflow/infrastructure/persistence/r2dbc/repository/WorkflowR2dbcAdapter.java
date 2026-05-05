package com.promptly.workflow.infrastructure.persistence.r2dbc.repository;

import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.domain.model.Workflow;
import com.promptly.workflow.domain.model.WorkflowStatus;
import com.promptly.workflow.infrastructure.persistence.r2dbc.entity.WorkflowR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * R2DBC adapter implementing {@link WorkflowPersistencePort} for SQL databases
 * (PostgreSQL, H2, SQLite).
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class WorkflowR2dbcAdapter implements WorkflowPersistencePort {

    private final WorkflowR2dbcRepository repository;

    @Override
    public Mono<Workflow> save(Workflow workflow) {
        return repository.save(toEntity(workflow)).map(this::toDomain);
    }

    @Override
    public Mono<Workflow> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Workflow> findByStatus(WorkflowStatus status) {
        return repository.findByStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Flux<Workflow> findByPromptId(String promptId) {
        return repository.findByPromptId(promptId).map(this::toDomain);
    }

    @Override
    public Flux<Workflow> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Flux<Workflow> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private WorkflowR2dbcEntity toEntity(Workflow workflow) {
        return WorkflowR2dbcEntity.builder()
                .id(workflow.getId())
                .projectId(workflow.getProjectId())
                .promptId(workflow.getPromptId())
                .promptVersion(workflow.getPromptVersion())
                .type(workflow.getType())
                .status(workflow.getStatus() != null ? workflow.getStatus().name() : null)
                .currentStep(workflow.getCurrentStep())
                .requestedBy(workflow.getRequestedBy())
                .version(workflow.getVersion() != null && workflow.getVersion() > 0 ? workflow.getVersion() : null)
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();
    }

    private Workflow toDomain(WorkflowR2dbcEntity entity) {
        return Workflow.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .promptId(entity.getPromptId())
                .promptVersion(entity.getPromptVersion())
                .type(entity.getType())
                .status(entity.getStatus() != null ? WorkflowStatus.valueOf(entity.getStatus()) : null)
                .currentStep(entity.getCurrentStep())
                .requestedBy(entity.getRequestedBy())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
