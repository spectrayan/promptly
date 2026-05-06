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
 * GenerateFromIdeaRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class GenerateFromIdeaRequest {

  private String idea;

  private @Nullable String projectId;

  public GenerateFromIdeaRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GenerateFromIdeaRequest(String idea) {
    this.idea = idea;
  }

  public GenerateFromIdeaRequest idea(String idea) {
    this.idea = idea;
    return this;
  }

  /**
   * A short natural-language description of the prompt the user wants to create
   * @return idea
   */
  @NotNull @Size(min = 5, max = 5000) 
  @Schema(name = "idea", example = "classify customer support tickets into billing, technical, and account categories", description = "A short natural-language description of the prompt the user wants to create", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("idea")
  public String getIdea() {
    return idea;
  }

  @JsonProperty("idea")
  public void setIdea(String idea) {
    this.idea = idea;
  }

  public GenerateFromIdeaRequest projectId(@Nullable String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Optional project context to ground the generation
   * @return projectId
   */
  @Size(max = 100) 
  @Schema(name = "projectId", description = "Optional project context to ground the generation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("projectId")
  public @Nullable String getProjectId() {
    return projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(@Nullable String projectId) {
    this.projectId = projectId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenerateFromIdeaRequest generateFromIdeaRequest = (GenerateFromIdeaRequest) o;
    return Objects.equals(this.idea, generateFromIdeaRequest.idea) &&
        Objects.equals(this.projectId, generateFromIdeaRequest.projectId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idea, projectId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerateFromIdeaRequest {\n");
    sb.append("    idea: ").append(toIndentedString(idea)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
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

