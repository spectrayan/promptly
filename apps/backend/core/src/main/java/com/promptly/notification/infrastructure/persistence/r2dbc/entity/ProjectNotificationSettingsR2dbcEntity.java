package com.promptly.notification.infrastructure.persistence.r2dbc.entity;

import io.r2dbc.postgresql.codec.Json;
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

    /** JSONB array of enabled event type keys — uses R2DBC PostgreSQL native Json codec. */
    @Column("enabled_events")
    private Json enabledEvents;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
