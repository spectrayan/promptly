package com.spectrayan.promptly.audit.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.audit.infrastructure.persistence.r2dbc.entity.AuditLogR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data R2DBC repository for the {@code audit_logs} table.
 */
public interface AuditLogR2dbcRepository extends R2dbcRepository<AuditLogR2dbcEntity, String> {

    Flux<AuditLogR2dbcEntity> findByResourceId(String resourceId);

    Flux<AuditLogR2dbcEntity> findByActorUserId(String actorUserId);

    Flux<AuditLogR2dbcEntity> findByProjectId(String projectId);

    Flux<AuditLogR2dbcEntity> findByAction(String action);
}
