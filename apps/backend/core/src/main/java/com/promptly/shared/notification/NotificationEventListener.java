package com.promptly.shared.notification;

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
 * Subscribes to domain events from all modules and dispatches
 * notifications via SSE (real-time UI) and email (async, best-effort).
 * <p>
 * Both channels are fire-and-forget — failures never impact the originating business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener {

    private final SseNotificationPort sse;
    private final EmailNotificationPort email;

    @EventListener
    void on(PromptCreated event) {
        if (event.projectId() == null) {
            log.warn("PromptCreated event missing projectId, skipping SSE");
            return;
        }
        sse.emit("project-" + event.projectId(), "prompt.created",
                payload("prompt.created",
                        "promptId", event.promptId(),
                        "name", event.name()));
    }

    @EventListener
    void on(PromptUpdated event) {
        if (event.projectId() == null) {
            log.warn("PromptUpdated event missing projectId, skipping SSE");
            return;
        }
        sse.emit("project-" + event.projectId(), "prompt.updated",
                payload("prompt.updated",
                        "promptId", event.promptId(),
                        "version", event.version()));
    }

    @EventListener
    void on(WorkflowApproved event) {
        if (event.projectId() == null) {
            log.warn("WorkflowApproved event missing projectId, skipping SSE");
            return;
        }
        sse.emit("project-" + event.projectId(), "workflow.approved",
                payload("workflow.approved",
                        "promptId", event.promptId(),
                        "approvedBy", event.approvedBy()));

        // Email the requester
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
            log.warn("WorkflowRejected event missing projectId, skipping SSE");
            return;
        }
        sse.emit("project-" + event.projectId(), "workflow.rejected",
                payload("workflow.rejected",
                        "promptId", event.promptId(),
                        "rejectedBy", event.rejectedBy(),
                        "reason", event.reason() != null ? event.reason() : ""));

        // Email the requester
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
            log.warn("ScanCompleted event missing projectId, skipping SSE");
            return;
        }
        String eventName = event.hasCriticalFindings() ? "scan.critical" : "scan.completed";
        sse.emit("project-" + event.projectId(), eventName,
                payload(eventName,
                        "promptId", event.promptId(),
                        "status", event.status(),
                        "score", event.overallScore()));
    }

    /**
     * Build a null-safe payload map with eventType included.
     * Map.of() throws on null values, so we use HashMap.
     */
    private Map<String, Object> payload(String eventType, Object... kvPairs) {
        var map = new HashMap<String, Object>();
        map.put("eventType", eventType);
        for (int i = 0; i < kvPairs.length - 1; i += 2) {
            String key = String.valueOf(kvPairs[i]);
            Object val = kvPairs[i + 1];
            map.put(key, val != null ? val : "");
        }
        return map;
    }
}
