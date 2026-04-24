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
 * UpdatePromptRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-23T20:26:14.636453-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class UpdatePromptRequest {

  private String content;

  private @Nullable String changeMessage;

  private String author;

  public UpdatePromptRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdatePromptRequest(String content, String author) {
    this.content = content;
    this.author = author;
  }

  public UpdatePromptRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Updated prompt content
   * @return content
   */
  @NotNull 
  @Schema(name = "content", description = "Updated prompt content", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(String content) {
    this.content = content;
  }

  public UpdatePromptRequest changeMessage(@Nullable String changeMessage) {
    this.changeMessage = changeMessage;
    return this;
  }

  /**
   * Description of what changed
   * @return changeMessage
   */
  
  @Schema(name = "changeMessage", example = "Added safety guardrails", description = "Description of what changed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("changeMessage")
  public @Nullable String getChangeMessage() {
    return changeMessage;
  }

  @JsonProperty("changeMessage")
  public void setChangeMessage(@Nullable String changeMessage) {
    this.changeMessage = changeMessage;
  }

  public UpdatePromptRequest author(String author) {
    this.author = author;
    return this;
  }

  /**
   * User making the update
   * @return author
   */
  @NotNull 
  @Schema(name = "author", description = "User making the update", requiredMode = Schema.RequiredMode.REQUIRED)
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
    UpdatePromptRequest updatePromptRequest = (UpdatePromptRequest) o;
    return Objects.equals(this.content, updatePromptRequest.content) &&
        Objects.equals(this.changeMessage, updatePromptRequest.changeMessage) &&
        Objects.equals(this.author, updatePromptRequest.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, changeMessage, author);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdatePromptRequest {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    changeMessage: ").append(toIndentedString(changeMessage)).append("\n");
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

