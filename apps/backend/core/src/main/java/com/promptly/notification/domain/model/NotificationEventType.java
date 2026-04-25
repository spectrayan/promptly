package com.promptly.notification.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of all known notification event types.
 * Provides metadata (icon, display title) used when creating notification records.
 */
@Getter
@RequiredArgsConstructor
public enum NotificationEventType {

    PROMPT_CREATED("prompt.created",        "edit_note",     "Prompt Created"),
    PROMPT_UPDATED("prompt.updated",        "update",        "Prompt Updated"),
    WORKFLOW_APPROVED("workflow.approved",   "check_circle",  "Workflow Approved"),
    WORKFLOW_REJECTED("workflow.rejected",   "cancel",        "Workflow Rejected"),
    SCAN_COMPLETED("scan.completed",        "verified",      "Scan Completed"),
    SCAN_CRITICAL("scan.critical",          "warning",       "Critical Findings");

    private final String key;
    private final String icon;
    private final String title;

    /**
     * Returns all event type keys — used as the default "all enabled" set.
     */
    public static Set<String> allKeys() {
        return Arrays.stream(values())
                .map(NotificationEventType::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Lookup by key string, returns null if not found.
     */
    public static NotificationEventType fromKey(String key) {
        for (NotificationEventType t : values()) {
            if (t.key.equals(key)) return t;
        }
        return null;
    }
}
