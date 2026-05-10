package com.spectrayan.promptly.notification.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Per-user, per-project notification preferences.
 * Controls which event types are muted and which channels are active.
 * <p>
 * Defaults: nothing muted, all channels enabled.
 */
@Data
@Builder
public class NotificationPreference {

    private String id;
    private String userId;
    private String projectId;

    /** Event types the user has silenced (e.g. "prompt.created"). */
    @Builder.Default
    private Set<String> mutedEvents = new HashSet<>();

    /** Master switch for in-app (bell icon) notifications. */
    @Builder.Default
    private boolean inAppEnabled = true;

    /** Master switch for email notifications. */
    @Builder.Default
    private boolean emailEnabled = true;

    private Instant updatedAt;

    /**
     * Check whether a given event type should be delivered via in-app channel.
     */
    public boolean shouldDeliverInApp(String eventType) {
        return inAppEnabled && !mutedEvents.contains(eventType);
    }

    /**
     * Check whether a given event type should be delivered via email channel.
     */
    public boolean shouldDeliverEmail(String eventType) {
        return emailEnabled && !mutedEvents.contains(eventType);
    }
}
