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
 * SearchResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-25T18:29:22.472644400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class SearchResponse {

  private @Nullable String promptId;

  private @Nullable String name;

  private @Nullable String description;

  private @Nullable Double score;

  public SearchResponse promptId(@Nullable String promptId) {
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

  public SearchResponse name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(@Nullable String name) {
    this.name = name;
  }

  public SearchResponse description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public SearchResponse score(@Nullable Double score) {
    this.score = score;
    return this;
  }

  /**
   * Relevance/similarity score (0.0–1.0)
   * @return score
   */
  
  @Schema(name = "score", description = "Relevance/similarity score (0.0–1.0)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("score")
  public @Nullable Double getScore() {
    return score;
  }

  @JsonProperty("score")
  public void setScore(@Nullable Double score) {
    this.score = score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchResponse searchResponse = (SearchResponse) o;
    return Objects.equals(this.promptId, searchResponse.promptId) &&
        Objects.equals(this.name, searchResponse.name) &&
        Objects.equals(this.description, searchResponse.description) &&
        Objects.equals(this.score, searchResponse.score);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptId, name, description, score);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchResponse {\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
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

