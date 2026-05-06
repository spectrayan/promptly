package com.promptly.project.infrastructure.persistence.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

/**
 * R2DBC entity for the {@code projects} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("projects")
public class ProjectR2dbcEntity {

    @Id
    private String id;

    private String name;
    private String description;

    /** JSON array of tags — converter handles List ↔ JSON string. */
    private List<String> tags;

    @Column("created_by")
    private String createdBy;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
