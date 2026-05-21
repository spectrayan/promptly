package com.spectrayan.promptly.notification.infrastructure.persistence.mongo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * MongoDB document for per-user, per-project notification preferences.
 */
@Data
@Builder
@Document(collection = "notification_preferences")
@CompoundIndex(name = "idx_user_project_unique", def = "{'userId': 1, 'projectId': 1}", unique = true)
public class NotificationPreferenceDocument {

    @Id
    private String id;

    private String userId;
    private String projectId;

    @Builder.Default
    private Set<String> mutedEvents = new HashSet<>();

    @Builder.Default
    private boolean inAppEnabled = true;

    @Builder.Default
    private boolean emailEnabled = true;

    private Instant updatedAt;
}
