package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * PromptBundleApproval
 */

@JsonTypeName("PromptBundle_approval")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class PromptBundleApproval {

  private @Nullable String workflowId;

  private @Nullable String approvedBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime approvedAt;

  public PromptBundleApproval workflowId(@Nullable String workflowId) {
    this.workflowId = workflowId;
    return this;
  }

  /**
   * Get workflowId
   * @return workflowId
   */
  
  @Schema(name = "workflowId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("workflowId")
  public @Nullable String getWorkflowId() {
    return workflowId;
  }

  @JsonProperty("workflowId")
  public void setWorkflowId(@Nullable String workflowId) {
    this.workflowId = workflowId;
  }

  public PromptBundleApproval approvedBy(@Nullable String approvedBy) {
    this.approvedBy = approvedBy;
    return this;
  }

  /**
   * Get approvedBy
   * @return approvedBy
   */
  
  @Schema(name = "approvedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("approvedBy")
  public @Nullable String getApprovedBy() {
    return approvedBy;
  }

  @JsonProperty("approvedBy")
  public void setApprovedBy(@Nullable String approvedBy) {
    this.approvedBy = approvedBy;
  }

  public PromptBundleApproval approvedAt(@Nullable OffsetDateTime approvedAt) {
    this.approvedAt = approvedAt;
    return this;
  }

  /**
   * Get approvedAt
   * @return approvedAt
   */
  @Valid 
  @Schema(name = "approvedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("approvedAt")
  public @Nullable OffsetDateTime getApprovedAt() {
    return approvedAt;
  }

  @JsonProperty("approvedAt")
  public void setApprovedAt(@Nullable OffsetDateTime approvedAt) {
    this.approvedAt = approvedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromptBundleApproval promptBundleApproval = (PromptBundleApproval) o;
    return Objects.equals(this.workflowId, promptBundleApproval.workflowId) &&
        Objects.equals(this.approvedBy, promptBundleApproval.approvedBy) &&
        Objects.equals(this.approvedAt, promptBundleApproval.approvedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workflowId, approvedBy, approvedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromptBundleApproval {\n");
    sb.append("    workflowId: ").append(toIndentedString(workflowId)).append("\n");
    sb.append("    approvedBy: ").append(toIndentedString(approvedBy)).append("\n");
    sb.append("    approvedAt: ").append(toIndentedString(approvedAt)).append("\n");
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

