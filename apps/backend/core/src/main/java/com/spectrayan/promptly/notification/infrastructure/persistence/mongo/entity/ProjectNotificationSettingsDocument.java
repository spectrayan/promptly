package com.spectrayan.promptly.notification.infrastructure.persistence.mongo.entity;

import com.spectrayan.promptly.notification.domain.model.NotificationEventType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

/**
 * MongoDB document for project-level notification settings.
 */
@Data
@Builder
@Document(collection = "notification_project_settings")
public class ProjectNotificationSettingsDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    @Builder.Default
    private Set<String> enabledEvents = NotificationEventType.allKeys();

    private Instant updatedAt;
}
