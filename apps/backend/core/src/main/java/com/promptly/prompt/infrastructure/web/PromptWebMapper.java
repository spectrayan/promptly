package com.promptly.prompt.infrastructure.web;

import com.promptly.infrastructure.in.web.dto.ContentFormat;
import com.promptly.infrastructure.in.web.dto.Environment;
import com.promptly.infrastructure.in.web.dto.PromptResponse;
import com.promptly.infrastructure.in.web.dto.PromptSummaryResponse;
import com.promptly.infrastructure.in.web.dto.VersionResponse;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptVersion;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Maps domain models to generated OpenAPI DTOs.
 * Intentionally manual (not MapStruct) to keep web-layer mapping simple and explicit.
 */
@Component
public class PromptWebMapper {

    public PromptResponse toPromptResponse(Prompt prompt) {
        String latestContent = prompt.getVersions().isEmpty()
                ? null
                : prompt.getVersions().get(prompt.getVersions().size() - 1).getContent();

        var response = new PromptResponse();
        response.setId(prompt.getId());
        response.setName(prompt.getName());
        response.setDescription(prompt.getDescription());
        response.setProjectId(prompt.getProjectId());
        response.setContentFormat(prompt.getContentFormat() != null
                ? ContentFormat.fromValue(prompt.getContentFormat().name())
                : null);
        response.setCurrentVersion(prompt.getCurrentVersion());
        response.setActiveEnvironment(prompt.getActiveEnvironment() != null
                ? Environment.fromValue(prompt.getActiveEnvironment())
                : null);
        response.setLatestContent(latestContent);
        response.setTags(prompt.getTags() != null ? new java.util.ArrayList<>(prompt.getTags()) : null);
        response.setCreatedAt(toOffsetDateTime(prompt.getCreatedAt()));
        response.setUpdatedAt(toOffsetDateTime(prompt.getUpdatedAt()));
        return response;
    }

    public PromptSummaryResponse toSummaryResponse(Prompt prompt) {
        var response = new PromptSummaryResponse();
        response.setId(prompt.getId());
        response.setName(prompt.getName());
        response.setDescription(prompt.getDescription());
        response.setProjectId(prompt.getProjectId());
        response.setCurrentVersion(prompt.getCurrentVersion());
        response.setActiveEnvironment(prompt.getActiveEnvironment());
        response.setUpdatedAt(toOffsetDateTime(prompt.getUpdatedAt()));
        return response;
    }

    public VersionResponse toVersionResponse(PromptVersion version) {
        var response = new VersionResponse();
        response.setVersionNumber(version.getVersionNumber());
        response.setContent(version.getContent());
        response.setChangeMessage(version.getChangeMessage());
        response.setCreatedBy(version.getCreatedBy());
        response.setCreatedAt(toOffsetDateTime(version.getCreatedAt()));
        return response;
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

}
