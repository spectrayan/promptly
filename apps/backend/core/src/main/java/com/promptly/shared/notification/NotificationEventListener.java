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
        sse.emit("project-" + event.projectId(), "prompt.created",
                Map.of("promptId", event.promptId(), "name", event.name()));
    }

    @EventListener
    void on(PromptUpdated event) {
        sse.emit("project-" + event.projectId(), "prompt.updated",
                Map.of("promptId", event.promptId(), "version", event.version()));
    }

    @EventListener
    void on(WorkflowApproved event) {
        sse.emit("project-" + event.projectId(), "workflow.approved",
                Map.of("promptId", event.promptId(), "approvedBy", event.approvedBy()));

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
        sse.emit("project-" + event.projectId(), "workflow.rejected",
                Map.of("promptId", event.promptId(), "rejectedBy", event.rejectedBy(),
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
        String eventName = event.hasCriticalFindings() ? "scan.critical" : "scan.completed";
        sse.emit("project-" + event.projectId(), eventName,
                Map.of("promptId", event.promptId(), "status", event.status(),
                       "score", event.overallScore()));
    }
}
