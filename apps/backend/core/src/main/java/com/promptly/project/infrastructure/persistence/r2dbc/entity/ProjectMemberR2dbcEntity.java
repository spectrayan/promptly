package com.promptly.project.infrastructure.persistence.r2dbc.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code project_members} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("project_members")
public class ProjectMemberR2dbcEntity {

    @Id
    private String id;

    @Column("project_id")
    private String projectId;

    @Column("user_id")
    private String userId;

    private String role;

    @Column("added_by")
    private String addedBy;

    @Column("added_at")
    private Instant addedAt;
}
