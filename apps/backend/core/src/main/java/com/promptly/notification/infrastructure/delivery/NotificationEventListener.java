package com.promptly.notification.infrastructure.delivery;

import com.promptly.notification.application.port.in.CreateNotificationUseCase;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.shared.domain.event.PromptCreated;
import com.promptly.shared.domain.event.PromptUpdated;
import com.promptly.shared.domain.event.ScanCompleted;
import com.promptly.shared.domain.event.WorkflowApproved;
import com.promptly.shared.domain.event.WorkflowRejected;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Central notification event listener.
 * <p>
 * Subscribes to domain events from all modules and delegates to
 * {@link CreateNotificationUseCase} which handles:
 * - Project settings check (is this event type enabled?)
 * - Member lookup (who should receive this notification?)
 * - User preference check (has this user muted this event type?)
 * - Persist to MongoDB
 * - SSE real-time push
 * - Email (when SMTP is configured)
 * <p>
 * Message content is derived from {@link NotificationEventType#resolveMessage(Map)}
 * templates — no hardcoded strings in this listener.
 * <p>
 * All operations are fire-and-forget — failures never impact the originating business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener {

    private final CreateNotificationUseCase createNotificationUseCase;
    private final EmailNotificationPort email;
    private final NotificationEmailTemplates emailTemplates;

    @EventListener
    void on(PromptCreated event) {
        if (event.projectId() == null) {
            log.warn("PromptCreated event missing projectId, skipping notification");
            return;
        }
        var payload = payload("promptId", event.promptId(), "name", event.name());
        fireNotification(event.projectId(), NotificationEventType.PROMPT_CREATED, payload);
    }

    @EventListener
    void on(PromptUpdated event) {
        if (event.projectId() == null) {
            log.warn("PromptUpdated event missing projectId, skipping notification");
            return;
        }
        var payload = payload("promptId", event.promptId(), "version", event.version());
        fireNotification(event.projectId(), NotificationEventType.PROMPT_UPDATED, payload);
    }

    @EventListener
    void on(WorkflowApproved event) {
        if (event.projectId() == null) {
            log.warn("WorkflowApproved event missing projectId, skipping notification");
            return;
        }
        var payload = payload("promptId", event.promptId(), "approvedBy", event.approvedBy());
        fireNotification(event.projectId(), NotificationEventType.WORKFLOW_APPROVED, payload);

        // Email the requester directly (outside the fan-out)
        if (event.requesterEmail() != null) {
            email.send(event.requesterEmail(),
                    "Prompt Approved: " + event.promptName(),
                    emailTemplates.approved(event.promptName(), event.approvedBy()));
        }
    }

    @EventListener
    void on(WorkflowRejected event) {
        if (event.projectId() == null) {
            log.warn("WorkflowRejected event missing projectId, skipping notification");
            return;
        }
        var payload = payload("promptId", event.promptId(),
                "rejectedBy", event.rejectedBy(),
                "reason", event.reason() != null ? event.reason() : "");
        fireNotification(event.projectId(), NotificationEventType.WORKFLOW_REJECTED, payload);

        // Email the requester directly
        if (event.requesterEmail() != null) {
            email.send(event.requesterEmail(),
                    "Prompt Rejected: " + event.promptName(),
                    emailTemplates.rejected(event.promptName(), event.rejectedBy(), event.reason()));
        }
    }

    @EventListener
    void on(ScanCompleted event) {
        if (event.projectId() == null) {
            log.warn("ScanCompleted event missing projectId, skipping notification");
            return;
        }
        NotificationEventType type = event.hasCriticalFindings()
                ? NotificationEventType.SCAN_CRITICAL
                : NotificationEventType.SCAN_COMPLETED;

        var payload = payload("promptId", event.promptId(),
                "status", event.status(), "score", event.overallScore());
        fireNotification(event.projectId(), type, payload);
    }

    // ── Internal ─────────────────────────────────────────────────────

    /**
     * Fire-and-forget notification creation.
     * Message is resolved from the event type's template.
     */
    private void fireNotification(String projectId, NotificationEventType type,
                                   Map<String, Object> payload) {
        String message = type.resolveMessage(payload);
        createNotificationUseCase.createForProject(projectId, type, message, payload)
                .doOnError(e -> log.warn("Notification fan-out failed: {}", e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }

    /**
     * Build a null-safe payload map.
     */
    private Map<String, Object> payload(Object... kvPairs) {
        var map = new HashMap<String, Object>();
        for (int i = 0; i < kvPairs.length - 1; i += 2) {
            String key = String.valueOf(kvPairs[i]);
            Object val = kvPairs[i + 1];
            map.put(key, val != null ? val : "");
        }
        return map;
    }
}
