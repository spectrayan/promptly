package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.ContentFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreatePromptRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-25T03:57:14.880945462-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class CreatePromptRequest {

  private String name;

  private @Nullable String description;

  private String projectId;

  private @Nullable ContentFormat contentFormat;

  private String content;

  private String author;

  public CreatePromptRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreatePromptRequest(String name, String projectId, String content, String author) {
    this.name = name;
    this.projectId = projectId;
    this.content = content;
    this.author = author;
  }

  public CreatePromptRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Human-readable prompt name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Care Plan Summary", description = "Human-readable prompt name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public CreatePromptRequest description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Brief description of the prompt's purpose
   * @return description
   */
  
  @Schema(name = "description", example = "Generates patient care plan summaries", description = "Brief description of the prompt's purpose", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public CreatePromptRequest projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Project/application this prompt belongs to
   * @return projectId
   */
  @NotNull 
  @Schema(name = "projectId", example = "healthcare", description = "Project/application this prompt belongs to", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("projectId")
  public String getProjectId() {
    return projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public CreatePromptRequest contentFormat(@Nullable ContentFormat contentFormat) {
    this.contentFormat = contentFormat;
    return this;
  }

  /**
   * Get contentFormat
   * @return contentFormat
   */
  @Valid 
  @Schema(name = "contentFormat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contentFormat")
  public @Nullable ContentFormat getContentFormat() {
    return contentFormat;
  }

  @JsonProperty("contentFormat")
  public void setContentFormat(@Nullable ContentFormat contentFormat) {
    this.contentFormat = contentFormat;
  }

  public CreatePromptRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * The prompt content (5KB–50KB supported)
   * @return content
   */
  @NotNull 
  @Schema(name = "content", example = "You are a clinical AI assistant...", description = "The prompt content (5KB–50KB supported)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(String content) {
    this.content = content;
  }

  public CreatePromptRequest author(String author) {
    this.author = author;
    return this;
  }

  /**
   * User who created the prompt
   * @return author
   */
  @NotNull 
  @Schema(name = "author", example = "admin", description = "User who created the prompt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("author")
  public String getAuthor() {
    return author;
  }

  @JsonProperty("author")
  public void setAuthor(String author) {
    this.author = author;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatePromptRequest createPromptRequest = (CreatePromptRequest) o;
    return Objects.equals(this.name, createPromptRequest.name) &&
        Objects.equals(this.description, createPromptRequest.description) &&
        Objects.equals(this.projectId, createPromptRequest.projectId) &&
        Objects.equals(this.contentFormat, createPromptRequest.contentFormat) &&
        Objects.equals(this.content, createPromptRequest.content) &&
        Objects.equals(this.author, createPromptRequest.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, projectId, contentFormat, content, author);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePromptRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    contentFormat: ").append(toIndentedString(contentFormat)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
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

