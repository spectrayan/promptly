package com.promptly.prompt.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents a single version of a prompt's content.
 * Immutable once created — new edits create new versions.
 */
@Getter
@Setter
@Builder
public class PromptVersion {

    private int versionNumber;
    private String content;
    private String changeMessage;
    private String createdBy;

    @Builder.Default
    private Instant createdAt = Instant.now();

}
