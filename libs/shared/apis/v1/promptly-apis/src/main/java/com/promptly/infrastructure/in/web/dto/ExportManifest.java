package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.promptly.infrastructure.in.web.dto.ExportManifestFilters;
import com.promptly.infrastructure.in.web.dto.ExportManifestSummary;
import com.promptly.infrastructure.in.web.dto.PromptBundle;
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
 * ExportManifest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ExportManifest {

  private @Nullable String manifestVersion;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime exportedAt;

  private @Nullable String exportedBy;

  private @Nullable ExportManifestFilters filters;

  private @Nullable String cursor;

  @Valid
  private List<@Valid PromptBundle> prompts = new ArrayList<>();

  private @Nullable ExportManifestSummary summary;

  private @Nullable String checksum;

  public ExportManifest manifestVersion(@Nullable String manifestVersion) {
    this.manifestVersion = manifestVersion;
    return this;
  }

  /**
   * Get manifestVersion
   * @return manifestVersion
   */
  
  @Schema(name = "manifestVersion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("manifestVersion")
  public @Nullable String getManifestVersion() {
    return manifestVersion;
  }

  @JsonProperty("manifestVersion")
  public void setManifestVersion(@Nullable String manifestVersion) {
    this.manifestVersion = manifestVersion;
  }

  public ExportManifest exportedAt(@Nullable OffsetDateTime exportedAt) {
    this.exportedAt = exportedAt;
    return this;
  }

  /**
   * Get exportedAt
   * @return exportedAt
   */
  @Valid 
  @Schema(name = "exportedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("exportedAt")
  public @Nullable OffsetDateTime getExportedAt() {
    return exportedAt;
  }

  @JsonProperty("exportedAt")
  public void setExportedAt(@Nullable OffsetDateTime exportedAt) {
    this.exportedAt = exportedAt;
  }

  public ExportManifest exportedBy(@Nullable String exportedBy) {
    this.exportedBy = exportedBy;
    return this;
  }

  /**
   * Get exportedBy
   * @return exportedBy
   */
  
  @Schema(name = "exportedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("exportedBy")
  public @Nullable String getExportedBy() {
    return exportedBy;
  }

  @JsonProperty("exportedBy")
  public void setExportedBy(@Nullable String exportedBy) {
    this.exportedBy = exportedBy;
  }

  public ExportManifest filters(@Nullable ExportManifestFilters filters) {
    this.filters = filters;
    return this;
  }

  /**
   * Get filters
   * @return filters
   */
  @Valid 
  @Schema(name = "filters", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filters")
  public @Nullable ExportManifestFilters getFilters() {
    return filters;
  }

  @JsonProperty("filters")
  public void setFilters(@Nullable ExportManifestFilters filters) {
    this.filters = filters;
  }

  public ExportManifest cursor(@Nullable String cursor) {
    this.cursor = cursor;
    return this;
  }

  /**
   * Get cursor
   * @return cursor
   */
  
  @Schema(name = "cursor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cursor")
  public @Nullable String getCursor() {
    return cursor;
  }

  @JsonProperty("cursor")
  public void setCursor(@Nullable String cursor) {
    this.cursor = cursor;
  }

  public ExportManifest prompts(List<@Valid PromptBundle> prompts) {
    this.prompts = prompts;
    return this;
  }

  public ExportManifest addPromptsItem(PromptBundle promptsItem) {
    if (this.prompts == null) {
      this.prompts = new ArrayList<>();
    }
    this.prompts.add(promptsItem);
    return this;
  }

  /**
   * Get prompts
   * @return prompts
   */
  @Valid 
  @Schema(name = "prompts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prompts")
  public List<@Valid PromptBundle> getPrompts() {
    return prompts;
  }

  @JsonProperty("prompts")
  public void setPrompts(List<@Valid PromptBundle> prompts) {
    this.prompts = prompts;
  }

  public ExportManifest summary(@Nullable ExportManifestSummary summary) {
    this.summary = summary;
    return this;
  }

  /**
   * Get summary
   * @return summary
   */
  @Valid 
  @Schema(name = "summary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("summary")
  public @Nullable ExportManifestSummary getSummary() {
    return summary;
  }

  @JsonProperty("summary")
  public void setSummary(@Nullable ExportManifestSummary summary) {
    this.summary = summary;
  }

  public ExportManifest checksum(@Nullable String checksum) {
    this.checksum = checksum;
    return this;
  }

  /**
   * Get checksum
   * @return checksum
   */
  
  @Schema(name = "checksum", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checksum")
  public @Nullable String getChecksum() {
    return checksum;
  }

  @JsonProperty("checksum")
  public void setChecksum(@Nullable String checksum) {
    this.checksum = checksum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExportManifest exportManifest = (ExportManifest) o;
    return Objects.equals(this.manifestVersion, exportManifest.manifestVersion) &&
        Objects.equals(this.exportedAt, exportManifest.exportedAt) &&
        Objects.equals(this.exportedBy, exportManifest.exportedBy) &&
        Objects.equals(this.filters, exportManifest.filters) &&
        Objects.equals(this.cursor, exportManifest.cursor) &&
        Objects.equals(this.prompts, exportManifest.prompts) &&
        Objects.equals(this.summary, exportManifest.summary) &&
        Objects.equals(this.checksum, exportManifest.checksum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(manifestVersion, exportedAt, exportedBy, filters, cursor, prompts, summary, checksum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExportManifest {\n");
    sb.append("    manifestVersion: ").append(toIndentedString(manifestVersion)).append("\n");
    sb.append("    exportedAt: ").append(toIndentedString(exportedAt)).append("\n");
    sb.append("    exportedBy: ").append(toIndentedString(exportedBy)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("    cursor: ").append(toIndentedString(cursor)).append("\n");
    sb.append("    prompts: ").append(toIndentedString(prompts)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    checksum: ").append(toIndentedString(checksum)).append("\n");
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

