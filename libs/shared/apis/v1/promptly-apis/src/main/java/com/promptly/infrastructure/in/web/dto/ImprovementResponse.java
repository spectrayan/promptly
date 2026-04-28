package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ImprovementResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T15:10:16.805941300-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ImprovementResponse {

  private @Nullable String promptId;

  private @Nullable String originalContent;

  private @Nullable String improvedContent;

  private @Nullable String summary;

  public ImprovementResponse promptId(@Nullable String promptId) {
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

  public ImprovementResponse originalContent(@Nullable String originalContent) {
    this.originalContent = originalContent;
    return this;
  }

  /**
   * Get originalContent
   * @return originalContent
   */
  
  @Schema(name = "originalContent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalContent")
  public @Nullable String getOriginalContent() {
    return originalContent;
  }

  @JsonProperty("originalContent")
  public void setOriginalContent(@Nullable String originalContent) {
    this.originalContent = originalContent;
  }

  public ImprovementResponse improvedContent(@Nullable String improvedContent) {
    this.improvedContent = improvedContent;
    return this;
  }

  /**
   * Get improvedContent
   * @return improvedContent
   */
  
  @Schema(name = "improvedContent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("improvedContent")
  public @Nullable String getImprovedContent() {
    return improvedContent;
  }

  @JsonProperty("improvedContent")
  public void setImprovedContent(@Nullable String improvedContent) {
    this.improvedContent = improvedContent;
  }

  public ImprovementResponse summary(@Nullable String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * Explanation of what was improved
   * @return summary
   */
  
  @Schema(name = "summary", description = "Explanation of what was improved", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("summary")
  public @Nullable String getSummary() {
    return summary;
  }

  @JsonProperty("summary")
  public void setSummary(@Nullable String summary) {
    this.summary = summary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImprovementResponse improvementResponse = (ImprovementResponse) o;
    return Objects.equals(this.promptId, improvementResponse.promptId) &&
        Objects.equals(this.originalContent, improvementResponse.originalContent) &&
        Objects.equals(this.improvedContent, improvementResponse.improvedContent) &&
        Objects.equals(this.summary, improvementResponse.summary);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptId, originalContent, improvedContent, summary);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImprovementResponse {\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    originalContent: ").append(toIndentedString(originalContent)).append("\n");
    sb.append("    improvedContent: ").append(toIndentedString(improvedContent)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
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

