package com.promptly.audit;

import com.promptly.AbstractIntegrationTest;
import com.promptly.audit.application.port.out.AuditRepository;
import com.promptly.shared.domain.DomainEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.test.ApplicationModuleTest;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Audit & Compliance module.
 * Verifies that audit entries are created automatically when domain events are published.
 */
@ApplicationModuleTest
class AuditModuleIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AuditRepository auditRepository;

    record DummyEvent(String aggregateId, Instant occurredAt, String someData) implements DomainEvent {}

    @Test
    void shouldCreateAuditEntryOnDomainEvent() {
        var event = new DummyEvent("test-resource-id", Instant.now(), "audit content");

        // Publish event manually to trigger the AuditAspect
        eventPublisher.publishEvent(event);

        // Wait briefly for async event processing, then verify audit entry
        StepVerifier.create(
                auditRepository.findByResourceId("test-resource-id")
                        .delaySubscription(Duration.ofMillis(500))
                        .collectList()
        )
        .assertNext(entries -> {
            assertThat(entries).isNotEmpty();
            assertThat(entries.get(0).getAction()).isEqualTo("dummy.event");
            assertThat(entries.get(0).getActorUserId()).isEqualTo("system");
        })
        .verifyComplete();
    }

}
