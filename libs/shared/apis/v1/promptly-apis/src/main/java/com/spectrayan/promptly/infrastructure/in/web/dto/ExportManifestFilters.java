package com.spectrayan.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * ExportManifestFilters
 */

@JsonTypeName("ExportManifest_filters")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ExportManifestFilters {

  private @Nullable String projectId;

  @Valid
  private List<String> promptIds = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime approvedAfter;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime approvedBefore;

  public ExportManifestFilters projectId(@Nullable String projectId) {
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

  public ExportManifestFilters promptIds(List<String> promptIds) {
    this.promptIds = promptIds;
    return this;
  }

  public ExportManifestFilters addPromptIdsItem(String promptIdsItem) {
    if (this.promptIds == null) {
      this.promptIds = new ArrayList<>();
    }
    this.promptIds.add(promptIdsItem);
    return this;
  }

  /**
   * Get promptIds
   * @return promptIds
   */
  
  @Schema(name = "promptIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promptIds")
  public List<String> getPromptIds() {
    return promptIds;
  }

  @JsonProperty("promptIds")
  public void setPromptIds(List<String> promptIds) {
    this.promptIds = promptIds;
  }

  public ExportManifestFilters approvedAfter(@Nullable OffsetDateTime approvedAfter) {
    this.approvedAfter = approvedAfter;
    return this;
  }

  /**
   * Get approvedAfter
   * @return approvedAfter
   */
  @Valid 
  @Schema(name = "approvedAfter", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("approvedAfter")
  public @Nullable OffsetDateTime getApprovedAfter() {
    return approvedAfter;
  }

  @JsonProperty("approvedAfter")
  public void setApprovedAfter(@Nullable OffsetDateTime approvedAfter) {
    this.approvedAfter = approvedAfter;
  }

  public ExportManifestFilters approvedBefore(@Nullable OffsetDateTime approvedBefore) {
    this.approvedBefore = approvedBefore;
    return this;
  }

  /**
   * Get approvedBefore
   * @return approvedBefore
   */
  @Valid 
  @Schema(name = "approvedBefore", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("approvedBefore")
  public @Nullable OffsetDateTime getApprovedBefore() {
    return approvedBefore;
  }

  @JsonProperty("approvedBefore")
  public void setApprovedBefore(@Nullable OffsetDateTime approvedBefore) {
    this.approvedBefore = approvedBefore;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExportManifestFilters exportManifestFilters = (ExportManifestFilters) o;
    return Objects.equals(this.projectId, exportManifestFilters.projectId) &&
        Objects.equals(this.promptIds, exportManifestFilters.promptIds) &&
        Objects.equals(this.approvedAfter, exportManifestFilters.approvedAfter) &&
        Objects.equals(this.approvedBefore, exportManifestFilters.approvedBefore);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, promptIds, approvedAfter, approvedBefore);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExportManifestFilters {\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    promptIds: ").append(toIndentedString(promptIds)).append("\n");
    sb.append("    approvedAfter: ").append(toIndentedString(approvedAfter)).append("\n");
    sb.append("    approvedBefore: ").append(toIndentedString(approvedBefore)).append("\n");
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

