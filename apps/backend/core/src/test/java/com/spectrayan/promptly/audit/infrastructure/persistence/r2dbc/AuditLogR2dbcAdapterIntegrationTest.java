package com.spectrayan.promptly.audit.infrastructure.persistence.r2dbc;

import com.spectrayan.promptly.AbstractR2dbcIntegrationTest;
import com.spectrayan.promptly.audit.application.port.out.AuditPersistencePort;
import com.spectrayan.promptly.audit.domain.model.AuditEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AuditLog R2DBC adapter.
 * Verifies append-only writes and query operations against PostgreSQL.
 */
@SpringBootTest
class AuditLogR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private AuditPersistencePort auditPort;

    private AuditEntry buildEntry(String action, String resourceId, String projectId) {
        return AuditEntry.builder()
                .id(UUID.randomUUID().toString())
                .projectId(projectId)
                .action(action)
                .resourceType("prompt")
                .resourceId(resourceId)
                .resourceVersion(1)
                .actorUserId("test-user")
                .actorEmail("test@example.com")
                .actorRole("ADMIN")
                .details(Map.of("field", "value", "count", 42))
                .timestamp(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldSaveAndFindByResourceId() {
        var entry = buildEntry("prompt.created", "resource-123", "audit-proj");

        StepVerifier.create(
                auditPort.save(entry)
                        .thenMany(auditPort.findByResourceId("resource-123"))
                        .collectList()
        )
        .assertNext(entries -> {
            assertThat(entries).isNotEmpty();
            var found = entries.get(0);
            assertThat(found.getAction()).isEqualTo("prompt.created");
            assertThat(found.getResourceId()).isEqualTo("resource-123");
            assertThat(found.getActorUserId()).isEqualTo("test-user");
            assertThat(found.getResourceType()).isEqualTo("prompt");
        })
        .verifyComplete();
    }

    @Test
    void shouldFindByActorUserId() {
        var uniqueUserId = "actor-" + UUID.randomUUID();
        var entry = AuditEntry.builder()
                .id(UUID.randomUUID().toString())
                .projectId("actor-proj")
                .action("prompt.updated")
                .resourceType("prompt")
                .resourceId("r-456")
                .actorUserId(uniqueUserId)
                .actorEmail("actor@example.com")
                .timestamp(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        StepVerifier.create(
                auditPort.save(entry)
                        .thenMany(auditPort.findByActorUserId(uniqueUserId))
                        .collectList()
        )
        .assertNext(entries -> {
            assertThat(entries).hasSize(1);
            assertThat(entries.get(0).getActorUserId()).isEqualTo(uniqueUserId);
        })
        .verifyComplete();
    }

    @Test
    void shouldFindByProjectId() {
        var projectId = "audit-proj-" + UUID.randomUUID();
        var entry = buildEntry("scan.completed", "r-scan", projectId);

        StepVerifier.create(
                auditPort.save(entry)
                        .thenMany(auditPort.findByProjectId(projectId))
                        .collectList()
        )
        .assertNext(entries -> {
            assertThat(entries).hasSizeGreaterThanOrEqualTo(1);
            assertThat(entries.get(0).getProjectId()).isEqualTo(projectId);
        })
        .verifyComplete();
    }

    @Test
    void shouldFindByAction() {
        var uniqueAction = "custom.action." + UUID.randomUUID();
        var entry = buildEntry(uniqueAction, "r-action", "action-proj");

        StepVerifier.create(
                auditPort.save(entry)
                        .thenMany(auditPort.findByAction(uniqueAction))
                        .collectList()
        )
        .assertNext(entries -> {
            assertThat(entries).hasSize(1);
            assertThat(entries.get(0).getAction()).isEqualTo(uniqueAction);
        })
        .verifyComplete();
    }

    @Test
    void shouldPersistDetailsAsJsonb() {
        var detailsMap = Map.<String, Object>of("key1", "value1", "nested", Map.of("inner", true));
        var entry = AuditEntry.builder()
                .id(UUID.randomUUID().toString())
                .projectId("jsonb-proj")
                .action("prompt.created")
                .resourceType("prompt")
                .resourceId("jsonb-resource")
                .actorUserId("jsonb-user")
                .details(detailsMap)
                .timestamp(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        StepVerifier.create(
                auditPort.save(entry)
                        .thenMany(auditPort.findByResourceId("jsonb-resource"))
                        .collectList()
        )
        .assertNext(entries -> {
            assertThat(entries.get(0).getDetails()).containsKey("key1");
        })
        .verifyComplete();
    }
}
