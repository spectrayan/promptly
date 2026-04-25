package com.promptly.prompt.infrastructure.persistence.entity;

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
import java.util.List;
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

    @Field("name")
    private String name;

    @Field("description")
    private String description;

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

    @Field("status")
    private String status;

    @Field("versions")
    private List<VersionSubdocument> versions;

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

    @Data
    @Builder
    public static class VersionSubdocument {
        private int versionNumber;
        private String content;
        private String changeMessage;
        private String createdBy;
        private Instant createdAt;
    }

}
