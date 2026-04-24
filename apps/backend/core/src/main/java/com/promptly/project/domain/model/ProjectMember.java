package com.promptly.project.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ProjectMember {
    private String id;
    private String projectId;
    private String userId;
    private String displayName;
    private String email;
    private ProjectRole role;
    private Instant joinedAt;
}
