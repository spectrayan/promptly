package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.WorkflowStatus;
import com.promptly.infrastructure.in.web.dto.WorkflowStepResponse;
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
 * WorkflowResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T16:27:22.798239400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class WorkflowResponse {

  private @Nullable String id;

  private @Nullable String promptId;

  private @Nullable Integer promptVersion;

  private @Nullable String type;

  private @Nullable WorkflowStatus status;

  private @Nullable Integer currentStep;

  private @Nullable String requestedBy;

  @Valid
  private List<@Valid WorkflowStepResponse> steps = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime updatedAt;

  public WorkflowResponse id(@Nullable String id) {
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

  public WorkflowResponse promptId(@Nullable String promptId) {
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

  public WorkflowResponse promptVersion(@Nullable Integer promptVersion) {
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

  public WorkflowResponse type(@Nullable String type) {
    this.type = type;
    return this;
  }

  /**
   * Workflow type (e.g., approval)
   * @return type
   */
  
  @Schema(name = "type", description = "Workflow type (e.g., approval)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public @Nullable String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(@Nullable String type) {
    this.type = type;
  }

  public WorkflowResponse status(@Nullable WorkflowStatus status) {
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
  public @Nullable WorkflowStatus getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(@Nullable WorkflowStatus status) {
    this.status = status;
  }

  public WorkflowResponse currentStep(@Nullable Integer currentStep) {
    this.currentStep = currentStep;
    return this;
  }

  /**
   * Get currentStep
   * @return currentStep
   */
  
  @Schema(name = "currentStep", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currentStep")
  public @Nullable Integer getCurrentStep() {
    return currentStep;
  }

  @JsonProperty("currentStep")
  public void setCurrentStep(@Nullable Integer currentStep) {
    this.currentStep = currentStep;
  }

  public WorkflowResponse requestedBy(@Nullable String requestedBy) {
    this.requestedBy = requestedBy;
    return this;
  }

  /**
   * Get requestedBy
   * @return requestedBy
   */
  
  @Schema(name = "requestedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requestedBy")
  public @Nullable String getRequestedBy() {
    return requestedBy;
  }

  @JsonProperty("requestedBy")
  public void setRequestedBy(@Nullable String requestedBy) {
    this.requestedBy = requestedBy;
  }

  public WorkflowResponse steps(List<@Valid WorkflowStepResponse> steps) {
    this.steps = steps;
    return this;
  }

  public WorkflowResponse addStepsItem(WorkflowStepResponse stepsItem) {
    if (this.steps == null) {
      this.steps = new ArrayList<>();
    }
    this.steps.add(stepsItem);
    return this;
  }

  /**
   * Get steps
   * @return steps
   */
  @Valid 
  @Schema(name = "steps", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("steps")
  public List<@Valid WorkflowStepResponse> getSteps() {
    return steps;
  }

  @JsonProperty("steps")
  public void setSteps(List<@Valid WorkflowStepResponse> steps) {
    this.steps = steps;
  }

  public WorkflowResponse createdAt(@Nullable OffsetDateTime createdAt) {
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

  public WorkflowResponse updatedAt(@Nullable OffsetDateTime updatedAt) {
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
    WorkflowResponse workflowResponse = (WorkflowResponse) o;
    return Objects.equals(this.id, workflowResponse.id) &&
        Objects.equals(this.promptId, workflowResponse.promptId) &&
        Objects.equals(this.promptVersion, workflowResponse.promptVersion) &&
        Objects.equals(this.type, workflowResponse.type) &&
        Objects.equals(this.status, workflowResponse.status) &&
        Objects.equals(this.currentStep, workflowResponse.currentStep) &&
        Objects.equals(this.requestedBy, workflowResponse.requestedBy) &&
        Objects.equals(this.steps, workflowResponse.steps) &&
        Objects.equals(this.createdAt, workflowResponse.createdAt) &&
        Objects.equals(this.updatedAt, workflowResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, promptId, promptVersion, type, status, currentStep, requestedBy, steps, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WorkflowResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    promptVersion: ").append(toIndentedString(promptVersion)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    currentStep: ").append(toIndentedString(currentStep)).append("\n");
    sb.append("    requestedBy: ").append(toIndentedString(requestedBy)).append("\n");
    sb.append("    steps: ").append(toIndentedString(steps)).append("\n");
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

