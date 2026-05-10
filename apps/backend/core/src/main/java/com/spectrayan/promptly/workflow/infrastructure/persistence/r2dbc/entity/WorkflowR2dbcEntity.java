package com.spectrayan.promptly.workflow.infrastructure.persistence.r2dbc.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code workflows} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("workflows")
public class WorkflowR2dbcEntity {

    @Id
    private String id;

    @Column("project_id")
    private String projectId;

    @Column("prompt_id")
    private String promptId;

    @Column("prompt_version")
    private int promptVersion;

    private String type;
    private String status;

    @Column("current_step")
    private int currentStep;

    @Column("requested_by")
    private String requestedBy;

    @Version
    private Long version;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
