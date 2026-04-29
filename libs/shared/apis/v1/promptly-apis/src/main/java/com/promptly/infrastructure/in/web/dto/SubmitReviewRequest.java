package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SubmitReviewRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-29T16:30:34.655679900-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class SubmitReviewRequest {

  private String promptId;

  private @Nullable String projectId;

  private Integer promptVersion;

  private String requestedBy;

  public SubmitReviewRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SubmitReviewRequest(String promptId, Integer promptVersion, String requestedBy) {
    this.promptId = promptId;
    this.promptVersion = promptVersion;
    this.requestedBy = requestedBy;
  }

  public SubmitReviewRequest promptId(String promptId) {
    this.promptId = promptId;
    return this;
  }

  /**
   * ID of the prompt to submit for review
   * @return promptId
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "promptId", description = "ID of the prompt to submit for review", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promptId")
  public String getPromptId() {
    return promptId;
  }

  @JsonProperty("promptId")
  public void setPromptId(String promptId) {
    this.promptId = promptId;
  }

  public SubmitReviewRequest projectId(@Nullable String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Project the prompt belongs to (for filtering)
   * @return projectId
   */
  @Size(max = 100) 
  @Schema(name = "projectId", description = "Project the prompt belongs to (for filtering)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("projectId")
  public @Nullable String getProjectId() {
    return projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(@Nullable String projectId) {
    this.projectId = projectId;
  }

  public SubmitReviewRequest promptVersion(Integer promptVersion) {
    this.promptVersion = promptVersion;
    return this;
  }

  /**
   * Version number of the prompt to review
   * minimum: 1
   * @return promptVersion
   */
  @NotNull @Min(value = 1) 
  @Schema(name = "promptVersion", description = "Version number of the prompt to review", requiredMode = Schema.RequiredMode.REQUIRED)
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
   * User requesting the review
   * @return requestedBy
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "requestedBy", description = "User requesting the review", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("requestedBy")
  public String getRequestedBy() {
    return requestedBy;
  }

  @JsonProperty("requestedBy")
  public void setRequestedBy(String requestedBy) {
    this.requestedBy = requestedBy;
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
        Objects.equals(this.projectId, submitReviewRequest.projectId) &&
        Objects.equals(this.promptVersion, submitReviewRequest.promptVersion) &&
        Objects.equals(this.requestedBy, submitReviewRequest.requestedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptId, projectId, promptVersion, requestedBy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubmitReviewRequest {\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    promptVersion: ").append(toIndentedString(promptVersion)).append("\n");
    sb.append("    requestedBy: ").append(toIndentedString(requestedBy)).append("\n");
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

