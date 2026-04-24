package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.Environment;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SubmitReviewRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-23T20:26:14.636453-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class SubmitReviewRequest {

  private String promptId;

  private Integer promptVersion;

  private String requestedBy;

  private Environment targetEnvironment;

  public SubmitReviewRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SubmitReviewRequest(String promptId, Integer promptVersion, String requestedBy, Environment targetEnvironment) {
    this.promptId = promptId;
    this.promptVersion = promptVersion;
    this.requestedBy = requestedBy;
    this.targetEnvironment = targetEnvironment;
  }

  public SubmitReviewRequest promptId(String promptId) {
    this.promptId = promptId;
    return this;
  }

  /**
   * Get promptId
   * @return promptId
   */
  @NotNull 
  @Schema(name = "promptId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promptId")
  public String getPromptId() {
    return promptId;
  }

  @JsonProperty("promptId")
  public void setPromptId(String promptId) {
    this.promptId = promptId;
  }

  public SubmitReviewRequest promptVersion(Integer promptVersion) {
    this.promptVersion = promptVersion;
    return this;
  }

  /**
   * Get promptVersion
   * @return promptVersion
   */
  @NotNull 
  @Schema(name = "promptVersion", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promptVersion")
  public Integer getPromptVersion() {
    return promptVersion;
  }

  @JsonProperty("promptVersion")
  public void setPromptVersion(Integer promptVersion) {
    this.promptVersion = promptVersion;
  }

  public SubmitReviewRequest requestedBy(String requestedBy) {
    this.requestedBy = requestedBy;
    return this;
  }

  /**
   * Get requestedBy
   * @return requestedBy
   */
  @NotNull 
  @Schema(name = "requestedBy", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("requestedBy")
  public String getRequestedBy() {
    return requestedBy;
  }

  @JsonProperty("requestedBy")
  public void setRequestedBy(String requestedBy) {
    this.requestedBy = requestedBy;
  }

  public SubmitReviewRequest targetEnvironment(Environment targetEnvironment) {
    this.targetEnvironment = targetEnvironment;
    return this;
  }

  /**
   * Get targetEnvironment
   * @return targetEnvironment
   */
  @NotNull @Valid 
  @Schema(name = "targetEnvironment", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("targetEnvironment")
  public Environment getTargetEnvironment() {
    return targetEnvironment;
  }

  @JsonProperty("targetEnvironment")
  public void setTargetEnvironment(Environment targetEnvironment) {
    this.targetEnvironment = targetEnvironment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmitReviewRequest submitReviewRequest = (SubmitReviewRequest) o;
    return Objects.equals(this.promptId, submitReviewRequest.promptId) &&
        Objects.equals(this.promptVersion, submitReviewRequest.promptVersion) &&
        Objects.equals(this.requestedBy, submitReviewRequest.requestedBy) &&
        Objects.equals(this.targetEnvironment, submitReviewRequest.targetEnvironment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptId, promptVersion, requestedBy, targetEnvironment);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubmitReviewRequest {\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    promptVersion: ").append(toIndentedString(promptVersion)).append("\n");
    sb.append("    requestedBy: ").append(toIndentedString(requestedBy)).append("\n");
    sb.append("    targetEnvironment: ").append(toIndentedString(targetEnvironment)).append("\n");
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

