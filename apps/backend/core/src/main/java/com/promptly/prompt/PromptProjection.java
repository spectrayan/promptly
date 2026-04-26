package com.promptly.prompt;

import java.time.Instant;

/**
 * Read-only projection of a Prompt, exposed across module boundaries
 * via {@link PromptModuleApi}.
 * <p>
 * This record replaces direct exposure of the {@code Prompt} aggregate,
 * ensuring that consumers in other modules cannot accidentally mutate
 * domain state or depend on internal aggregate structure.
 */
public record PromptProjection(
        String id,
        String name,
        String description,
        String projectId,
        String status,
        int currentVersion,
        String latestContent,
        String contentFormat,
        Instant createdAt,
        Instant updatedAt
) {}
