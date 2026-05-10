package com.spectrayan.promptly.notification.infrastructure.persistence.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;

/**
 * R2DBC entity for the {@code notification_preferences} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("notification_preferences")
public class NotificationPreferenceR2dbcEntity {

    @Id
    private String id;

    @Column("user_id")
    private String userId;

    @Column("project_id")
    private String projectId;

    /** JSON array of muted event type keys — converter handles Set ↔ JSON string. */
    @Column("muted_events")
    private Set<String> mutedEvents;

    @Column("in_app_enabled")
    private boolean inAppEnabled;

    @Column("email_enabled")
    private boolean emailEnabled;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
