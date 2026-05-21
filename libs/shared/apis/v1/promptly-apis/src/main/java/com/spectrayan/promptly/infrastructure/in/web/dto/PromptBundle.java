package com.spectrayan.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.spectrayan.promptly.infrastructure.in.web.dto.PromptBundleApproval;
import com.spectrayan.promptly.infrastructure.in.web.dto.PromptBundleScan;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PromptBundle
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class PromptBundle {

  private @Nullable String promptId;

  private @Nullable String name;

  private @Nullable Integer version;

  private @Nullable String content;

  private @Nullable String contentFormat;

  private @Nullable String projectId;

  @Valid
  private Map<String, Object> metadata = new HashMap<>();

  private @Nullable PromptBundleApproval approval;

  private @Nullable PromptBundleScan scan;

  public PromptBundle promptId(@Nullable String promptId) {
    this.promptId = promptId;
    return this;
  }

  /**
   * Get promptId
   * @return promptId
   */
  
  @Schema(name = "promptId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promptId")
  public @Nullable String getPromptId() {
    return promptId;
  }

  @JsonProperty("promptId")
  public void setPromptId(@Nullable String promptId) {
    this.promptId = promptId;
  }

  public PromptBundle name(@Nullable String name) {
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

  public PromptBundle version(@Nullable Integer version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
   */
  
  @Schema(name = "version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public @Nullable Integer getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(@Nullable Integer version) {
    this.version = version;
  }

  public PromptBundle content(@Nullable String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
   */
  
  @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public @Nullable String getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(@Nullable String content) {
    this.content = content;
  }

  public PromptBundle contentFormat(@Nullable String contentFormat) {
    this.contentFormat = contentFormat;
    return this;
  }

  /**
   * Get contentFormat
   * @return contentFormat
   */
  
  @Schema(name = "contentFormat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contentFormat")
  public @Nullable String getContentFormat() {
    return contentFormat;
  }

  @JsonProperty("contentFormat")
  public void setContentFormat(@Nullable String contentFormat) {
    this.contentFormat = contentFormat;
  }

  public PromptBundle projectId(@Nullable String projectId) {
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

  public PromptBundle metadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return this;
  }

  public PromptBundle putMetadataItem(String key, Object metadataItem) {
    if (this.metadata == null) {
      this.metadata = new HashMap<>();
    }
    this.metadata.put(key, metadataItem);
    return this;
  }

  /**
   * Get metadata
   * @return metadata
   */
  
  @Schema(name = "metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("metadata")
  public Map<String, Object> getMetadata() {
    return metadata;
  }

  @JsonProperty("metadata")
  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public PromptBundle approval(@Nullable PromptBundleApproval approval) {
    this.approval = approval;
    return this;
  }

  /**
   * Get approval
   * @return approval
   */
  @Valid 
  @Schema(name = "approval", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("approval")
  public @Nullable PromptBundleApproval getApproval() {
    return approval;
  }

  @JsonProperty("approval")
  public void setApproval(@Nullable PromptBundleApproval approval) {
    this.approval = approval;
  }

  public PromptBundle scan(@Nullable PromptBundleScan scan) {
    this.scan = scan;
    return this;
  }

  /**
   * Get scan
   * @return scan
   */
  @Valid 
  @Schema(name = "scan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scan")
  public @Nullable PromptBundleScan getScan() {
    return scan;
  }

  @JsonProperty("scan")
  public void setScan(@Nullable PromptBundleScan scan) {
    this.scan = scan;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromptBundle promptBundle = (PromptBundle) o;
    return Objects.equals(this.promptId, promptBundle.promptId) &&
        Objects.equals(this.name, promptBundle.name) &&
        Objects.equals(this.version, promptBundle.version) &&
        Objects.equals(this.content, promptBundle.content) &&
        Objects.equals(this.contentFormat, promptBundle.contentFormat) &&
        Objects.equals(this.projectId, promptBundle.projectId) &&
        Objects.equals(this.metadata, promptBundle.metadata) &&
        Objects.equals(this.approval, promptBundle.approval) &&
        Objects.equals(this.scan, promptBundle.scan);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptId, name, version, content, contentFormat, projectId, metadata, approval, scan);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromptBundle {\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    contentFormat: ").append(toIndentedString(contentFormat)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    approval: ").append(toIndentedString(approval)).append("\n");
    sb.append("    scan: ").append(toIndentedString(scan)).append("\n");
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

