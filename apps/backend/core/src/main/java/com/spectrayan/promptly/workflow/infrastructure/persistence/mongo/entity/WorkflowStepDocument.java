package com.spectrayan.promptly.workflow.infrastructure.persistence.mongo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * MongoDB document for the {@code workflow_steps} collection.
 * Each document represents a single step in a workflow's approval chain.
 * <p>
 * Steps are stored separately from the workflow document to avoid
 * embedded arrays and enable SQL-compatible persistence.
 */
@Data
@Builder
@Document(collection = "workflow_steps")
@CompoundIndex(name = "idx_wfstep_workflow_step", def = "{'workflowId': 1, 'step': 1}", unique = true)
public class WorkflowStepDocument {

    @Id
    private String id;

    @Field("workflowId")
    private String workflowId;

    @Field("step")
    private int step;

    @Field("role")
    private String role;

    @Field("assignedTo")
    private String assignedTo;

    @Field("action")
    private String action;

    @Field("comment")
    private String comment;

    @Field("actedAt")
    private Instant actedAt;
}
