package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.ContentFormat;
import com.promptly.infrastructure.in.web.dto.PromptStatus;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PromptResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-29T16:30:34.655679900-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class PromptResponse {

  private @Nullable String id;

  private @Nullable String name;

  private @Nullable String description;

  private @Nullable String projectId;

  private @Nullable PromptStatus status;

  private @Nullable ContentFormat contentFormat;

  private @Nullable Integer currentVersion;

  private @Nullable String latestContent;

  @Valid
  private List<String> tags = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime updatedAt;

  public PromptResponse id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique prompt ID (MongoDB ObjectId)
   * @return id
   */
  
  @Schema(name = "id", description = "Unique prompt ID (MongoDB ObjectId)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(@Nullable String id) {
    this.id = id;
  }

  public PromptResponse name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(@Nullable String name) {
    this.name = name;
  }

  public PromptResponse description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public PromptResponse projectId(@Nullable String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Get projectId
   * @return projectId
   */
  
  @Schema(name = "projectId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("projectId")
  public @Nullable String getProjectId() {
    return projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(@Nullable String projectId) {
    this.projectId = projectId;
  }

  public PromptResponse status(@Nullable PromptStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable PromptStatus getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(@Nullable PromptStatus status) {
    this.status = status;
  }

  public PromptResponse contentFormat(@Nullable ContentFormat contentFormat) {
    this.contentFormat = contentFormat;
    return this;
  }

  /**
   * Get contentFormat
   * @return contentFormat
   */
  @Valid 
  @Schema(name = "contentFormat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contentFormat")
  public @Nullable ContentFormat getContentFormat() {
    return contentFormat;
  }

  @JsonProperty("contentFormat")
  public void setContentFormat(@Nullable ContentFormat contentFormat) {
    this.contentFormat = contentFormat;
  }

  public PromptResponse currentVersion(@Nullable Integer currentVersion) {
    this.currentVersion = currentVersion;
    return this;
  }

  /**
   * Latest version number
   * @return currentVersion
   */
  
  @Schema(name = "currentVersion", description = "Latest version number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currentVersion")
  public @Nullable Integer getCurrentVersion() {
    return currentVersion;
  }

  @JsonProperty("currentVersion")
  public void setCurrentVersion(@Nullable Integer currentVersion) {
    this.currentVersion = currentVersion;
  }

  public PromptResponse latestContent(@Nullable String latestContent) {
    this.latestContent = latestContent;
    return this;
  }

  /**
   * Content of the latest version
   * @return latestContent
   */
  
  @Schema(name = "latestContent", description = "Content of the latest version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("latestContent")
  public @Nullable String getLatestContent() {
    return latestContent;
  }

  @JsonProperty("latestContent")
  public void setLatestContent(@Nullable String latestContent) {
    this.latestContent = latestContent;
  }

  public PromptResponse tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public PromptResponse addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Get tags
   * @return tags
   */
  
  @Schema(name = "tags", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }

  @JsonProperty("tags")
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public PromptResponse createdAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @Valid 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdAt")
  public @Nullable OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("createdAt")
  public void setCreatedAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public PromptResponse updatedAt(@Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @Valid 
  @Schema(name = "updatedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updatedAt")
  public @Nullable OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  @JsonProperty("updatedAt")
  public void setUpdatedAt(@Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromptResponse promptResponse = (PromptResponse) o;
    return Objects.equals(this.id, promptResponse.id) &&
        Objects.equals(this.name, promptResponse.name) &&
        Objects.equals(this.description, promptResponse.description) &&
        Objects.equals(this.projectId, promptResponse.projectId) &&
        Objects.equals(this.status, promptResponse.status) &&
        Objects.equals(this.contentFormat, promptResponse.contentFormat) &&
        Objects.equals(this.currentVersion, promptResponse.currentVersion) &&
        Objects.equals(this.latestContent, promptResponse.latestContent) &&
        Objects.equals(this.tags, promptResponse.tags) &&
        Objects.equals(this.createdAt, promptResponse.createdAt) &&
        Objects.equals(this.updatedAt, promptResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, projectId, status, contentFormat, currentVersion, latestContent, tags, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromptResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    contentFormat: ").append(toIndentedString(contentFormat)).append("\n");
    sb.append("    currentVersion: ").append(toIndentedString(currentVersion)).append("\n");
    sb.append("    latestContent: ").append(toIndentedString(latestContent)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

