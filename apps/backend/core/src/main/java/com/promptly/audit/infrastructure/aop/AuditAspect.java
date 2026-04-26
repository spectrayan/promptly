package com.promptly.audit.infrastructure.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptly.audit.application.port.out.AuditPersistencePort;
import com.promptly.audit.domain.model.AuditEntry;
import com.promptly.shared.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AOP-style generic audit capture via {@code @EventListener}.
 * <p>
 * Automatically captures ALL {@link DomainEvent} publications and persists
 * audit entries without requiring manual listener methods for each event type.
 * <p>
 * Uses a single generic {@code @EventListener(DomainEvent.class)} to handle
 * any current and future event types automatically.
 * <p>
 * Event type naming convention: The simple class name is converted from
 * {@code PromptCreated} → {@code prompt.created} for the audit action field.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditPersistencePort auditRepository;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules();

    /**
     * Generic event listener that captures ALL domain events for audit.
     * Runs asynchronously — fires after the event is published successfully.
     * All errors are caught and logged (audit never breaks business logic).
     */
    @EventListener
    public void auditDomainEvent(DomainEvent event) {
        try {
            String action = toActionName(event.getClass());
            String resourceType = toResourceType(event.getClass());

            Map<String, Object> details = extractDetails(event);

            AuditEntry entry = AuditEntry.builder()
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(event.aggregateId())
                    .actorUserId("system")
                    .details(details)
                    .timestamp(event.occurredAt() != null ? event.occurredAt() : Instant.now())
                    .build();

            auditRepository.save(entry)
                    .doOnError(e -> log.warn("Audit save failed (non-fatal): action={}, error={}",
                            action, e.getMessage()))
                    .onErrorComplete()
                    .subscribe();

            log.debug("Audit captured: action={}, resourceId={}", action, event.aggregateId());

        } catch (Exception e) {
            log.warn("Audit capture failed (non-fatal): {}", e.getMessage());
        }
    }

    /**
     * Converts event class name to audit action.
     * {@code PromptCreated} → {@code prompt.created}
     * {@code WorkflowApproved} → {@code workflow.approved}
     */
    private String toActionName(Class<?> eventClass) {
        String name = eventClass.getSimpleName();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                if (Character.isLowerCase(name.charAt(i - 1))) {
                    sb.append('.');
                }
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * Derives resource type from the event's package name.
     * {@code com.promptly.prompt.PromptCreated} → {@code prompt}
     */
    private String toResourceType(Class<?> eventClass) {
        String pkg = eventClass.getPackageName();
        String[] parts = pkg.split("\\.");
        return parts.length >= 3 ? parts[2] : "unknown";
    }

    /**
     * Extracts event record fields into a map for the audit details.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractDetails(DomainEvent event) {
        try {
            Map<String, Object> allFields = OBJECT_MAPPER.convertValue(event, Map.class);
            Map<String, Object> details = new LinkedHashMap<>(allFields);
            details.remove("aggregateId");
            details.remove("occurredAt");
            return details;
        } catch (Exception e) {
            return Map.of("eventType", event.getClass().getSimpleName());
        }
    }
}
