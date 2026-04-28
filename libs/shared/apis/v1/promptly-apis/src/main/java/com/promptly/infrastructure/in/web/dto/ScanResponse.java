package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.FindingResponse;
import com.promptly.infrastructure.in.web.dto.ScanStatus;
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
 * ScanResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T16:27:22.798239400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ScanResponse {

  private @Nullable String id;

  private @Nullable String promptId;

  private @Nullable Integer promptVersion;

  private @Nullable Double overallScore;

  private @Nullable ScanStatus status;

  @Valid
  private List<@Valid FindingResponse> findings = new ArrayList<>();

  private @Nullable String llmProvider;

  private @Nullable String llmModel;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime scannedAt;

  public ScanResponse id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(@Nullable String id) {
    this.id = id;
  }

  public ScanResponse promptId(@Nullable String promptId) {
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

  public ScanResponse promptVersion(@Nullable Integer promptVersion) {
    this.promptVersion = promptVersion;
    return this;
  }

  /**
   * Get promptVersion
   * @return promptVersion
   */
  
  @Schema(name = "promptVersion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promptVersion")
  public @Nullable Integer getPromptVersion() {
    return promptVersion;
  }

  @JsonProperty("promptVersion")
  public void setPromptVersion(@Nullable Integer promptVersion) {
    this.promptVersion = promptVersion;
  }

  public ScanResponse overallScore(@Nullable Double overallScore) {
    this.overallScore = overallScore;
    return this;
  }

  /**
   * Severity score (0.0 = safe, 10.0 = critical)
   * @return overallScore
   */
  
  @Schema(name = "overallScore", description = "Severity score (0.0 = safe, 10.0 = critical)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("overallScore")
  public @Nullable Double getOverallScore() {
    return overallScore;
  }

  @JsonProperty("overallScore")
  public void setOverallScore(@Nullable Double overallScore) {
    this.overallScore = overallScore;
  }

  public ScanResponse status(@Nullable ScanStatus status) {
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
  public @Nullable ScanStatus getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(@Nullable ScanStatus status) {
    this.status = status;
  }

  public ScanResponse findings(List<@Valid FindingResponse> findings) {
    this.findings = findings;
    return this;
  }

  public ScanResponse addFindingsItem(FindingResponse findingsItem) {
    if (this.findings == null) {
      this.findings = new ArrayList<>();
    }
    this.findings.add(findingsItem);
    return this;
  }

  /**
   * Get findings
   * @return findings
   */
  @Valid 
  @Schema(name = "findings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("findings")
  public List<@Valid FindingResponse> getFindings() {
    return findings;
  }

  @JsonProperty("findings")
  public void setFindings(List<@Valid FindingResponse> findings) {
    this.findings = findings;
  }

  public ScanResponse llmProvider(@Nullable String llmProvider) {
    this.llmProvider = llmProvider;
    return this;
  }

  /**
   * Get llmProvider
   * @return llmProvider
   */
  
  @Schema(name = "llmProvider", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("llmProvider")
  public @Nullable String getLlmProvider() {
    return llmProvider;
  }

  @JsonProperty("llmProvider")
  public void setLlmProvider(@Nullable String llmProvider) {
    this.llmProvider = llmProvider;
  }

  public ScanResponse llmModel(@Nullable String llmModel) {
    this.llmModel = llmModel;
    return this;
  }

  /**
   * Get llmModel
   * @return llmModel
   */
  
  @Schema(name = "llmModel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("llmModel")
  public @Nullable String getLlmModel() {
    return llmModel;
  }

  @JsonProperty("llmModel")
  public void setLlmModel(@Nullable String llmModel) {
    this.llmModel = llmModel;
  }

  public ScanResponse scannedAt(@Nullable OffsetDateTime scannedAt) {
    this.scannedAt = scannedAt;
    return this;
  }

  /**
   * Get scannedAt
   * @return scannedAt
   */
  @Valid 
  @Schema(name = "scannedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scannedAt")
  public @Nullable OffsetDateTime getScannedAt() {
    return scannedAt;
  }

  @JsonProperty("scannedAt")
  public void setScannedAt(@Nullable OffsetDateTime scannedAt) {
    this.scannedAt = scannedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScanResponse scanResponse = (ScanResponse) o;
    return Objects.equals(this.id, scanResponse.id) &&
        Objects.equals(this.promptId, scanResponse.promptId) &&
        Objects.equals(this.promptVersion, scanResponse.promptVersion) &&
        Objects.equals(this.overallScore, scanResponse.overallScore) &&
        Objects.equals(this.status, scanResponse.status) &&
        Objects.equals(this.findings, scanResponse.findings) &&
        Objects.equals(this.llmProvider, scanResponse.llmProvider) &&
        Objects.equals(this.llmModel, scanResponse.llmModel) &&
        Objects.equals(this.scannedAt, scanResponse.scannedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, promptId, promptVersion, overallScore, status, findings, llmProvider, llmModel, scannedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ScanResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    promptVersion: ").append(toIndentedString(promptVersion)).append("\n");
    sb.append("    overallScore: ").append(toIndentedString(overallScore)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    findings: ").append(toIndentedString(findings)).append("\n");
    sb.append("    llmProvider: ").append(toIndentedString(llmProvider)).append("\n");
    sb.append("    llmModel: ").append(toIndentedString(llmModel)).append("\n");
    sb.append("    scannedAt: ").append(toIndentedString(scannedAt)).append("\n");
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

