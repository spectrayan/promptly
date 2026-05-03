package com.promptly.audit.application.listener;

import com.promptly.audit.application.port.out.AuditPersistencePort;
import com.promptly.audit.domain.model.AuditEntry;
import com.promptly.shared.domain.event.PromptCreated;
import com.promptly.shared.domain.event.PromptRolledBack;
import com.promptly.shared.domain.event.PromptUpdated;
import com.promptly.shared.domain.event.ScanCompleted;
import com.promptly.shared.domain.event.ReviewSubmitted;
import com.promptly.shared.domain.event.WorkflowApproved;
import com.promptly.shared.domain.event.WorkflowRejected;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

/**
 * Listens to ALL domain events across modules and persists audit entries.
 * This is the central audit trail — it consumes events but never produces them.
 * Uses @Async to avoid blocking the source modules.
 *
 * @deprecated Replaced by {@link com.promptly.audit.infrastructure.aop.AuditAspect}
 * which automatically captures all DomainEvent publications via AOP.
 * Kept temporarily for reference during migration.
 */
@Slf4j
// @Service — disabled: replaced by AuditAspect generic handler
@RequiredArgsConstructor
@Deprecated(since = "0.0.2", forRemoval = true)
public class AuditEventListener {

    private final AuditPersistencePort auditRepository;

    @Async
    @EventListener
    void on(PromptCreated event) {
        log.debug("Audit: PromptCreated {}", event.aggregateId());
        save("prompt.created", "prompt", event.aggregateId(), event.version(),
                Map.of("name", event.promptName()));
    }

    @Async
    @EventListener
    void on(PromptUpdated event) {
        log.debug("Audit: PromptUpdated {}", event.aggregateId());
        save("prompt.updated", "prompt", event.aggregateId(), event.version(), Map.of());
    }

    @Async
    @EventListener
    void on(PromptRolledBack event) {
        log.debug("Audit: PromptRolledBack {}", event.aggregateId());
        save("prompt.rolled_back", "prompt", event.aggregateId(), event.toVersion(),
                Map.of("fromVersion", event.fromVersion(), "toVersion", event.toVersion()));
    }

    @Async
    @EventListener
    void on(ReviewSubmitted event) {
        log.debug("Audit: ReviewSubmitted {}", event.aggregateId());
        save("workflow.submitted", "workflow", event.aggregateId(), null,
                Map.of("promptId", event.promptId(), "requestedBy", event.requestedBy()));
    }

    @Async
    @EventListener
    void on(WorkflowApproved event) {
        log.debug("Audit: WorkflowApproved {}", event.aggregateId());
        save("workflow.approved", "workflow", event.aggregateId(), null,
                Map.of("promptId", event.promptId(),
                        "approvedBy", event.approvedBy()));
    }

    @Async
    @EventListener
    void on(WorkflowRejected event) {
        log.debug("Audit: WorkflowRejected {}", event.aggregateId());
        save("workflow.rejected", "workflow", event.aggregateId(), null,
                Map.of("promptId", event.promptId(), "rejectedBy", event.rejectedBy(),
                        "reason", event.reason()));
    }

    @Async
    @EventListener
    void on(ScanCompleted event) {
        log.debug("Audit: ScanCompleted {}", event.aggregateId());
        save("scan.completed", "scan", event.aggregateId(), null,
                Map.of("promptId", event.promptId(), "status", event.status(),
                        "score", event.overallScore()));
    }

    private void save(String action, String resourceType, String resourceId,
                       Integer resourceVersion, Map<String, Object> details) {
        AuditEntry entry = AuditEntry.builder()
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .resourceVersion(resourceVersion)
                .actorUserId("system")
                .details(details)
                .timestamp(Instant.now())
                .build();

        auditRepository.save(entry)
                .doOnError(e -> log.warn("Failed to save audit entry: {}", e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }

}
