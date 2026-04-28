package com.promptly.workflow.infrastructure.persistence.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB document for the Workflow aggregate.
 */
@Data
@Builder
@Document(collection = "workflows")
public class WorkflowDocument {

    @Id
    private String id;

    @NotBlank
    @Size(max = 100)
    private String projectId;

    @NotBlank
    @Indexed
    private String promptId;

    private int promptVersion;

    @NotBlank
    private String type;

    @NotNull
    private String status;
    private int currentStep;

    @NotBlank
    @Size(max = 100)
    private String requestedBy;
    private List<WorkflowStepSubdocument> steps;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @Builder
    public static class WorkflowStepSubdocument {
        private int step;
        private String role;
        private String assignedTo;
        private String action;
        private String comment;
        private Instant actedAt;
    }

}
