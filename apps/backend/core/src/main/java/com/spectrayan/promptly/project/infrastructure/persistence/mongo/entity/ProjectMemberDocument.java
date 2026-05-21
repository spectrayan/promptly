package com.spectrayan.promptly.project.infrastructure.persistence.mongo.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "project_members")
@CompoundIndex(name = "idx_project_user", def = "{'projectId': 1, 'userId': 1}", unique = true)
public class ProjectMemberDocument {
    @Id
    private String id;

    @NotBlank
    @Size(max = 100)
    private String projectId;

    @NotBlank
    @Size(max = 100)
    private String userId;

    @NotNull
    private String role;

    @NotBlank
    @Size(max = 100)
    private String addedBy;
    private Instant addedAt;
}
