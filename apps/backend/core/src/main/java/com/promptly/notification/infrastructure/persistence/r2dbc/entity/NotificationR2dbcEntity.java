package com.promptly.notification.infrastructure.persistence.r2dbc.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code notifications} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("notifications")
public class NotificationR2dbcEntity {

    @Id
    private String id;

    @Column("user_id")
    private String userId;

    @Column("project_id")
    private String projectId;

    private String type;
    private String title;
    private String message;
    private String icon;

    /** JSONB payload — uses R2DBC PostgreSQL native Json codec. */
    private Json payload;

    @Column("is_read")
    private boolean read;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;
}
