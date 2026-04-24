package com.promptly.audit;

import com.promptly.AbstractIntegrationTest;
import com.promptly.audit.application.port.out.AuditRepository;
import com.promptly.prompt.application.port.in.CreatePromptUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Audit & Compliance module.
 * Verifies that audit entries are created automatically when domain events are published.
 */
@ApplicationModuleTest
class AuditModuleIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CreatePromptUseCase createPromptUseCase;

    @Autowired
    private AuditRepository auditRepository;

    @Test
    void shouldCreateAuditEntryOnPromptCreated() {
        var command = new CreatePromptUseCase.CreatePromptCommand(
                "Audit Test Prompt", "For audit testing",
                "audit-project", "TEXT", "Audit content", "audit-user"
        );

        // Create a prompt → triggers PromptCreated event → async audit listener persists
        var prompt = createPromptUseCase.createPrompt(command).block(Duration.ofSeconds(5));
        assertThat(prompt).isNotNull();

        // Wait briefly for async event processing, then verify audit entry
        StepVerifier.create(
                auditRepository.findByResourceId(prompt.getId())
                        .delaySubscription(Duration.ofMillis(500))
                        .collectList()
        )
        .assertNext(entries -> {
            assertThat(entries).isNotEmpty();
            assertThat(entries.get(0).getAction()).isEqualTo("prompt.created");
            assertThat(entries.get(0).getActorUserId()).isEqualTo("audit-user");
        })
        .verifyComplete();
    }

}
