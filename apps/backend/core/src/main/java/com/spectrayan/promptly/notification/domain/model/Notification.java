package com.spectrayan.promptly.notification.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * Core notification entity.
 * Represents a single notification delivered to a specific user within a project context.
 * Pure POJO — no framework annotations.
 */
@Data
@Builder
public class Notification {

    private String id;
    private String userId;
    private String projectId;
    private String type;                    // e.g. "workflow.approved"
    private String title;                   // "Workflow Approved"
    private String message;                 // "Prompt 'X' approved by alice@promptly.ai"
    private String icon;                    // Material icon name
    private Map<String, Object> payload;    // Raw event data
    private boolean read;
    private Instant createdAt;

    public void markAsRead() {
        this.read = true;
    }
}
