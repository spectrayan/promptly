package com.spectrayan.promptly.notification.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of all known notification event types.
 * <p>
 * Provides metadata (icon, display title, message template) used when creating
 * notification records. Message templates use {@code {key}} placeholders that
 * are resolved against the event payload map.
 */
@Getter
@RequiredArgsConstructor
public enum NotificationEventType {

    PROMPT_CREATED("prompt.created",      "edit_note",     "Prompt Created",
            "Prompt \"{promptName}\" was created"),

    PROMPT_UPDATED("prompt.updated",      "update",        "Prompt Updated",
            "Prompt \"{promptName}\" updated to version {version}"),

    WORKFLOW_APPROVED("workflow.approved", "check_circle",  "Workflow Approved",
            "Approved by {approvedBy}"),

    WORKFLOW_REJECTED("workflow.rejected", "cancel",        "Workflow Rejected",
            "Rejected by {rejectedBy}: {reason}"),

    SCAN_COMPLETED("scan.completed",      "verified",      "Scan Completed",
            "Prompt \"{promptName}\" scan finished with score {score}"),

    SCAN_CRITICAL("scan.critical",        "warning",       "Critical Findings",
            "Prompt \"{promptName}\" critical findings detected! Score: {score}");

    private final String key;
    private final String icon;
    private final String title;
    private final String messageTemplate;

    /**
     * Resolves the message template against a payload map.
     * <p>
     * Replaces all {@code {key}} placeholders with values from the map.
     * Missing keys are replaced with empty strings.
     *
     * @param payload the event payload containing template variable values
     * @return the resolved human-readable message
     */
    public String resolveMessage(Map<String, Object> payload) {
        String result = messageTemplate;
        if (payload != null) {
            for (var entry : payload.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? String.valueOf(entry.getValue()) : "";
                result = result.replace(placeholder, value);
            }
        }
        // Clean up any remaining unresolved placeholders
        result = result.replaceAll("\\{[^}]+}", "").trim();
        // Clean up orphaned colons from empty optional fields (e.g., ": " when reason is empty)
        result = result.replaceAll(":\\s*$", "").trim();
        return result;
    }

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
