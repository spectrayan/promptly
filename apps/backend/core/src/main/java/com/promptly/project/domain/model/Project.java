package com.promptly.project.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class Project {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
