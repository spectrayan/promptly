package com.spectrayan.promptly.notification.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

/**
 * Per-project notification settings.
 * Controls which event types are enabled for the entire project (master switch).
 * Managed by project admins/owners.
 * <p>
 * Default: all event types enabled.
 */
@Data
@Builder
public class ProjectNotificationSettings {

    private String id;
    private String projectId;

    /** The set of event type keys that are enabled for this project. */
    @Builder.Default
    private Set<String> enabledEvents = NotificationEventType.allKeys();

    private Instant updatedAt;

    /**
     * Check whether a given event type is enabled at the project level.
     */
    public boolean isEventEnabled(String eventType) {
        return enabledEvents.contains(eventType);
    }
}
