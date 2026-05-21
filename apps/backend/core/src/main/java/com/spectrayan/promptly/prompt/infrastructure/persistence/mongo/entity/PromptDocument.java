package com.spectrayan.promptly.prompt.infrastructure.persistence.mongo.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Set;

/**
 * MongoDB document for the Prompt aggregate.
 * Framework annotations (@Document, @Id, etc.) live HERE — never on domain models.
 */
@Data
@Builder
@Document(collection = "prompts")
@CompoundIndex(name = "idx_project_name", def = "{'projectId': 1, 'name': 1}", unique = true)
public class PromptDocument {

    @Id
    private String id;

    @NotBlank
    @Size(max = 200)
    @Field("name")
    private String name;

    @Size(max = 1000)
    @Field("description")
    private String description;

    @NotBlank
    @Size(max = 100)
    @Field("projectId")
    private String projectId;

    @Field("contentFormat")
    private String contentFormat;

    @Field("tags")
    private Set<String> tags;

    @Field("metadata")
    private MetadataSubdocument metadata;

    @Field("currentVersion")
    private int currentVersion;

    @Field("content")
    private String content;

    @NotNull
    @Field("status")
    private String status;

    @Version
    private Long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    @Data
    @Builder
    public static class MetadataSubdocument {
        private String model;
        private Double temperature;
        private Integer maxTokens;
        private String systemContext;
    }

}
