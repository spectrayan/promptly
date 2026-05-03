package com.promptly.project.infrastructure.persistence.mongo.entity;

import jakarta.validation.constraints.NotBlank;
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

@Data
@Builder
@Document(collection = "projects")
public class ProjectDocument {
    @Id
    private String id;

    @NotBlank
    @Size(max = 200)
    @Indexed(unique = true)
    private String name;

    @Size(max = 1000)
    private String description;
    private List<String> tags;

    @NotBlank
    @Size(max = 100)
    private String createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
