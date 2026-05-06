package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ExportManifestSummary
 */

@JsonTypeName("ExportManifest_summary")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ExportManifestSummary {

  private @Nullable Integer totalPrompts;

  private @Nullable String projectId;

  private @Nullable String projectName;

  public ExportManifestSummary totalPrompts(@Nullable Integer totalPrompts) {
    this.totalPrompts = totalPrompts;
    return this;
  }

  /**
   * Get totalPrompts
   * @return totalPrompts
   */
  
  @Schema(name = "totalPrompts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalPrompts")
  public @Nullable Integer getTotalPrompts() {
    return totalPrompts;
  }

  @JsonProperty("totalPrompts")
  public void setTotalPrompts(@Nullable Integer totalPrompts) {
    this.totalPrompts = totalPrompts;
  }

  public ExportManifestSummary projectId(@Nullable String projectId) {
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

  public ExportManifestSummary projectName(@Nullable String projectName) {
    this.projectName = projectName;
    return this;
  }

  /**
   * Get projectName
   * @return projectName
   */
  
  @Schema(name = "projectName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("projectName")
  public @Nullable String getProjectName() {
    return projectName;
  }

  @JsonProperty("projectName")
  public void setProjectName(@Nullable String projectName) {
    this.projectName = projectName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExportManifestSummary exportManifestSummary = (ExportManifestSummary) o;
    return Objects.equals(this.totalPrompts, exportManifestSummary.totalPrompts) &&
        Objects.equals(this.projectId, exportManifestSummary.projectId) &&
        Objects.equals(this.projectName, exportManifestSummary.projectName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalPrompts, projectId, projectName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExportManifestSummary {\n");
    sb.append("    totalPrompts: ").append(toIndentedString(totalPrompts)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    projectName: ").append(toIndentedString(projectName)).append("\n");
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

