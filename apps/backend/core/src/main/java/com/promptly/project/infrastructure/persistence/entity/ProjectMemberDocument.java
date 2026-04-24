package com.promptly.project.infrastructure.persistence.entity;

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
    private String projectId;
    private String userId;
    private String displayName;
    private String email;
    private String role;
    private Instant joinedAt;
}
