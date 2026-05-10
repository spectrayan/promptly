package com.spectrayan.promptly.prompt.infrastructure.persistence.mongo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * MongoDB document for the {@code prompt_history} collection.
 * Each document represents a single version of a prompt's content.
 * <p>
 * Versions are stored separately from the prompt document to avoid
 * document bloat and enable on-demand loading.
 */
@Data
@Builder
@Document(collection = "prompt_history")
public class PromptHistoryDocument {

    @Id
    private String id;

    @Field("promptId")
    private String promptId;

    @Field("versionNumber")
    private int versionNumber;

    @Field("content")
    private String content;

    @Field("changeMessage")
    private String changeMessage;

    @Field("createdBy")
    private String createdBy;

    @CreatedDate
    private Instant createdAt;
}
