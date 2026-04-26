package com.promptly.audit.application.port.out;

import com.promptly.audit.domain.model.AuditEntry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for audit log persistence.
 * Write-only in application code — no update/delete operations.
 */
public interface AuditPersistencePort {

    Mono<AuditEntry> save(AuditEntry entry);

    Flux<AuditEntry> findByResourceId(String resourceId);

    Flux<AuditEntry> findByActorUserId(String userId);

    Flux<AuditEntry> findAll();

    Flux<AuditEntry> findByProjectId(String projectId);

    Flux<AuditEntry> findByAction(String action);

}
