package com.promptly.shared.notification;

import com.promptly.notification.application.port.in.NotificationUseCase;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.prompt.PromptCreated;
import com.promptly.prompt.PromptUpdated;
import com.promptly.scanner.ScanCompleted;
import com.promptly.workflow.WorkflowApproved;
import com.promptly.workflow.WorkflowRejected;
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
 * {@link NotificationUseCase} which handles:
 * - Project settings check (is this event type enabled?)
 * - Member lookup (who should receive this notification?)
 * - User preference check (has this user muted this event type?)
 * - Persist to MongoDB
 * - SSE real-time push
 * - Email (when SMTP is configured)
 * <p>
 * All operations are fire-and-forget — failures never impact the originating business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationUseCase notificationUseCase;
    private final EmailNotificationPort email;

    @EventListener
    void on(PromptCreated event) {
        if (event.projectId() == null) {
            log.warn("PromptCreated event missing projectId, skipping notification");
            return;
        }
        notificationUseCase.createForProject(
                event.projectId(),
                NotificationEventType.PROMPT_CREATED,
                "Prompt \"" + event.name() + "\" was created",
                payload("promptId", event.promptId(), "name", event.name())
        ).subscribe();
    }

    @EventListener
    void on(PromptUpdated event) {
        if (event.projectId() == null) {
            log.warn("PromptUpdated event missing projectId, skipping notification");
            return;
        }
        notificationUseCase.createForProject(
                event.projectId(),
                NotificationEventType.PROMPT_UPDATED,
                "Prompt updated to version " + event.version(),
                payload("promptId", event.promptId(), "version", event.version())
        ).subscribe();
    }

    @EventListener
    void on(WorkflowApproved event) {
        if (event.projectId() == null) {
            log.warn("WorkflowApproved event missing projectId, skipping notification");
            return;
        }
        notificationUseCase.createForProject(
                event.projectId(),
                NotificationEventType.WORKFLOW_APPROVED,
                "Approved by " + (event.approvedBy() != null ? event.approvedBy() : "unknown"),
                payload("promptId", event.promptId(), "approvedBy", event.approvedBy())
        ).subscribe();

        // Email the requester directly (outside the fan-out)
        if (event.requesterEmail() != null) {
            email.send(event.requesterEmail(),
                    "Prompt Approved: " + event.promptName(),
                    "<h3>Your prompt has been approved</h3>" +
                    "<p><b>" + event.promptName() + "</b> was approved by " + event.approvedBy() + ".</p>");
        }
    }

    @EventListener
    void on(WorkflowRejected event) {
        if (event.projectId() == null) {
            log.warn("WorkflowRejected event missing projectId, skipping notification");
            return;
        }
        notificationUseCase.createForProject(
                event.projectId(),
                NotificationEventType.WORKFLOW_REJECTED,
                "Rejected by " + (event.rejectedBy() != null ? event.rejectedBy() : "unknown") +
                        (event.reason() != null ? ": " + event.reason() : ""),
                payload("promptId", event.promptId(), "rejectedBy", event.rejectedBy(),
                        "reason", event.reason() != null ? event.reason() : "")
        ).subscribe();

        // Email the requester directly
        if (event.requesterEmail() != null) {
            email.send(event.requesterEmail(),
                    "Prompt Rejected: " + event.promptName(),
                    "<h3>Your prompt was rejected</h3>" +
                    "<p><b>" + event.promptName() + "</b> was rejected by " + event.rejectedBy() + ".</p>" +
                    "<p>Reason: " + (event.reason() != null ? event.reason() : "No reason provided") + "</p>");
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

        String message = event.hasCriticalFindings()
                ? "Critical findings detected! Score: " + event.overallScore()
                : "Scan finished with score " + event.overallScore();

        notificationUseCase.createForProject(
                event.projectId(),
                type,
                message,
                payload("promptId", event.promptId(), "status", event.status(),
                        "score", event.overallScore())
        ).subscribe();
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
