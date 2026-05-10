package com.spectrayan.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ImportResultResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ImportResultResponse {

  private @Nullable String status;

  private @Nullable Integer imported;

  private @Nullable Integer failed;

  @Valid
  private List<String> importedPromptIds = new ArrayList<>();

  private @Nullable String error;

  public ImportResultResponse status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public ImportResultResponse imported(@Nullable Integer imported) {
    this.imported = imported;
    return this;
  }

  /**
   * Get imported
   * @return imported
   */
  
  @Schema(name = "imported", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("imported")
  public @Nullable Integer getImported() {
    return imported;
  }

  @JsonProperty("imported")
  public void setImported(@Nullable Integer imported) {
    this.imported = imported;
  }

  public ImportResultResponse failed(@Nullable Integer failed) {
    this.failed = failed;
    return this;
  }

  /**
   * Get failed
   * @return failed
   */
  
  @Schema(name = "failed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("failed")
  public @Nullable Integer getFailed() {
    return failed;
  }

  @JsonProperty("failed")
  public void setFailed(@Nullable Integer failed) {
    this.failed = failed;
  }

  public ImportResultResponse importedPromptIds(List<String> importedPromptIds) {
    this.importedPromptIds = importedPromptIds;
    return this;
  }

  public ImportResultResponse addImportedPromptIdsItem(String importedPromptIdsItem) {
    if (this.importedPromptIds == null) {
      this.importedPromptIds = new ArrayList<>();
    }
    this.importedPromptIds.add(importedPromptIdsItem);
    return this;
  }

  /**
   * Get importedPromptIds
   * @return importedPromptIds
   */
  
  @Schema(name = "importedPromptIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("importedPromptIds")
  public List<String> getImportedPromptIds() {
    return importedPromptIds;
  }

  @JsonProperty("importedPromptIds")
  public void setImportedPromptIds(List<String> importedPromptIds) {
    this.importedPromptIds = importedPromptIds;
  }

  public ImportResultResponse error(@Nullable String error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
   */
  
  @Schema(name = "error", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error")
  public @Nullable String getError() {
    return error;
  }

  @JsonProperty("error")
  public void setError(@Nullable String error) {
    this.error = error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImportResultResponse importResultResponse = (ImportResultResponse) o;
    return Objects.equals(this.status, importResultResponse.status) &&
        Objects.equals(this.imported, importResultResponse.imported) &&
        Objects.equals(this.failed, importResultResponse.failed) &&
        Objects.equals(this.importedPromptIds, importResultResponse.importedPromptIds) &&
        Objects.equals(this.error, importResultResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, imported, failed, importedPromptIds, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImportResultResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    imported: ").append(toIndentedString(imported)).append("\n");
    sb.append("    failed: ").append(toIndentedString(failed)).append("\n");
    sb.append("    importedPromptIds: ").append(toIndentedString(importedPromptIds)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

