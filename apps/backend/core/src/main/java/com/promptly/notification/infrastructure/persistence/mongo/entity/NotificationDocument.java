package com.promptly.notification.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * MongoDB document for notifications.
 * Framework annotations live here — never on domain models.
 */
@Data
@Builder
@Document(collection = "notifications")
@CompoundIndex(name = "idx_user_project_read_time", def = "{'userId': 1, 'projectId': 1, 'read': 1, 'createdAt': -1}")
public class NotificationDocument {

    @Id
    private String id;

    private String userId;
    private String projectId;
    private String type;
    private String title;
    private String message;
    private String icon;
    private Map<String, Object> payload;
    private boolean read;

    @CreatedDate
    private Instant createdAt;
}
