package com.spectrayan.promptly.infrastructure.in.web.dto;

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
 * ApplyImprovementRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ApplyImprovementRequest {

  private String improvedContent;

  private String author;

  public ApplyImprovementRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ApplyImprovementRequest(String improvedContent, String author) {
    this.improvedContent = improvedContent;
    this.author = author;
  }

  public ApplyImprovementRequest improvedContent(String improvedContent) {
    this.improvedContent = improvedContent;
    return this;
  }

  /**
   * The improved prompt content to apply
   * @return improvedContent
   */
  @NotNull @Size(min = 1, max = 51200) 
  @Schema(name = "improvedContent", description = "The improved prompt content to apply", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("improvedContent")
  public String getImprovedContent() {
    return improvedContent;
  }

  @JsonProperty("improvedContent")
  public void setImprovedContent(String improvedContent) {
    this.improvedContent = improvedContent;
  }

  public ApplyImprovementRequest author(String author) {
    this.author = author;
    return this;
  }

  /**
   * User applying the improvement
   * @return author
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "author", description = "User applying the improvement", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ApplyImprovementRequest applyImprovementRequest = (ApplyImprovementRequest) o;
    return Objects.equals(this.improvedContent, applyImprovementRequest.improvedContent) &&
        Objects.equals(this.author, applyImprovementRequest.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(improvedContent, author);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplyImprovementRequest {\n");
    sb.append("    improvedContent: ").append(toIndentedString(improvedContent)).append("\n");
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

