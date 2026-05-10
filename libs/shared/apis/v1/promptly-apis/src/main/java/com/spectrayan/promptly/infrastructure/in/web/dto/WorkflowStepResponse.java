package com.spectrayan.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.spectrayan.promptly.infrastructure.in.web.dto.StepAction;
import java.time.OffsetDateTime;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * WorkflowStepResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class WorkflowStepResponse {

  private @Nullable Integer step;

  private @Nullable String role;

  private @Nullable String assignedTo;

  private @Nullable StepAction action;

  private @Nullable String comment;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime actedAt;

  public WorkflowStepResponse step(@Nullable Integer step) {
    this.step = step;
    return this;
  }

  /**
   * Get step
   * @return step
   */
  
  @Schema(name = "step", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("step")
  public @Nullable Integer getStep() {
    return step;
  }

  @JsonProperty("step")
  public void setStep(@Nullable Integer step) {
    this.step = step;
  }

  public WorkflowStepResponse role(@Nullable String role) {
    this.role = role;
    return this;
  }

  /**
   * Required role (e.g., reviewer, approver)
   * @return role
   */
  
  @Schema(name = "role", description = "Required role (e.g., reviewer, approver)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("role")
  public @Nullable String getRole() {
    return role;
  }

  @JsonProperty("role")
  public void setRole(@Nullable String role) {
    this.role = role;
  }

  public WorkflowStepResponse assignedTo(@Nullable String assignedTo) {
    this.assignedTo = assignedTo;
    return this;
  }

  /**
   * Get assignedTo
   * @return assignedTo
   */
  
  @Schema(name = "assignedTo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("assignedTo")
  public @Nullable String getAssignedTo() {
    return assignedTo;
  }

  @JsonProperty("assignedTo")
  public void setAssignedTo(@Nullable String assignedTo) {
    this.assignedTo = assignedTo;
  }

  public WorkflowStepResponse action(@Nullable StepAction action) {
    this.action = action;
    return this;
  }

  /**
   * Get action
   * @return action
   */
  @Valid 
  @Schema(name = "action", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("action")
  public @Nullable StepAction getAction() {
    return action;
  }

  @JsonProperty("action")
  public void setAction(@Nullable StepAction action) {
    this.action = action;
  }

  public WorkflowStepResponse comment(@Nullable String comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Get comment
   * @return comment
   */
  
  @Schema(name = "comment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("comment")
  public @Nullable String getComment() {
    return comment;
  }

  @JsonProperty("comment")
  public void setComment(@Nullable String comment) {
    this.comment = comment;
  }

  public WorkflowStepResponse actedAt(@Nullable OffsetDateTime actedAt) {
    this.actedAt = actedAt;
    return this;
  }

  /**
   * Get actedAt
   * @return actedAt
   */
  @Valid 
  @Schema(name = "actedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actedAt")
  public @Nullable OffsetDateTime getActedAt() {
    return actedAt;
  }

  @JsonProperty("actedAt")
  public void setActedAt(@Nullable OffsetDateTime actedAt) {
    this.actedAt = actedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkflowStepResponse workflowStepResponse = (WorkflowStepResponse) o;
    return Objects.equals(this.step, workflowStepResponse.step) &&
        Objects.equals(this.role, workflowStepResponse.role) &&
        Objects.equals(this.assignedTo, workflowStepResponse.assignedTo) &&
        Objects.equals(this.action, workflowStepResponse.action) &&
        Objects.equals(this.comment, workflowStepResponse.comment) &&
        Objects.equals(this.actedAt, workflowStepResponse.actedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(step, role, assignedTo, action, comment, actedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WorkflowStepResponse {\n");
    sb.append("    step: ").append(toIndentedString(step)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    assignedTo: ").append(toIndentedString(assignedTo)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
    sb.append("    actedAt: ").append(toIndentedString(actedAt)).append("\n");
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

