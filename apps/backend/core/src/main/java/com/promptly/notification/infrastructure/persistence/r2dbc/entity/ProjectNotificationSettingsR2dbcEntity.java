package com.promptly.notification.infrastructure.persistence.r2dbc.entity;

import com.promptly.shared.config.r2dbc.converter.JsonColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code project_notification_settings} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("project_notification_settings")
public class ProjectNotificationSettingsR2dbcEntity {

    @Id
    private String id;

    @Column("project_id")
    private String projectId;

    /** JSON array of enabled event type keys — JSONB on PostgreSQL, JSON on H2. */
    @Column("enabled_events")
    private JsonColumn enabledEvents;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
