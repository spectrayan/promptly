package com.spectrayan.promptly.project.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ProjectMember {
    private String id;
    private String projectId;
    private String userId;
    private ProjectRole role;
    private String addedBy;
    private Instant addedAt;
}
