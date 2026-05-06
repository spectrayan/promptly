package com.promptly.audit.infrastructure.persistence.r2dbc.repository;

import com.promptly.audit.application.port.out.AuditPersistencePort;
import com.promptly.audit.domain.model.AuditEntry;
import com.promptly.audit.infrastructure.persistence.r2dbc.entity.AuditLogR2dbcEntity;
import com.promptly.shared.config.r2dbc.converter.JsonColumn;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * R2DBC adapter implementing {@link AuditPersistencePort} for SQL databases.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class AuditLogR2dbcAdapter implements AuditPersistencePort {

    private final AuditLogR2dbcRepository repository;

    @Override
    public Mono<AuditEntry> save(AuditEntry entry) {
        return repository.save(toEntity(entry)).map(this::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByResourceId(String resourceId) {
        return repository.findByResourceId(resourceId).map(this::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByActorUserId(String userId) {
        return repository.findByActorUserId(userId).map(this::toDomain);
    }

    @Override
    public Flux<AuditEntry> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByAction(String action) {
        return repository.findByAction(action).map(this::toDomain);
    }

    private AuditLogR2dbcEntity toEntity(AuditEntry entry) {
        return AuditLogR2dbcEntity.builder()
                .projectId(entry.getProjectId())
                .action(entry.getAction())
                .resourceType(entry.getResourceType())
                .resourceId(entry.getResourceId())
                .resourceVersion(entry.getResourceVersion())
                .actorUserId(entry.getActorUserId())
                .actorEmail(entry.getActorEmail())
                .actorRole(entry.getActorRole())
                .details(JsonColumn.of(entry.getDetails()))
                .timestamp(entry.getTimestamp())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }

    private AuditEntry toDomain(AuditLogR2dbcEntity entity) {
        Map<String, Object> details = entity.getDetails() != null
                ? entity.getDetails().toMap()
                : null;

        return AuditEntry.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .action(entity.getAction())
                .resourceType(entity.getResourceType())
                .resourceId(entity.getResourceId())
                .resourceVersion(entity.getResourceVersion())
                .actorUserId(entity.getActorUserId())
                .actorEmail(entity.getActorEmail())
                .actorRole(entity.getActorRole())
                .details(details)
                .timestamp(entity.getTimestamp())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
