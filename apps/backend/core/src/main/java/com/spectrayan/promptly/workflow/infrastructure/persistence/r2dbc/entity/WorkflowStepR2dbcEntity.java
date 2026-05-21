package com.spectrayan.promptly.workflow.infrastructure.persistence.r2dbc.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code workflow_steps} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("workflow_steps")
public class WorkflowStepR2dbcEntity {

    @Id
    private String id;

    @Column("workflow_id")
    private String workflowId;

    private int step;
    private String role;

    @Column("assigned_to")
    private String assignedTo;

    private String action;
    private String comment;

    @Column("acted_at")
    private Instant actedAt;
}
