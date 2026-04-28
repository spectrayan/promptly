package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.promptly.infrastructure.in.web.dto.SearchResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DuplicateCheckResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T16:27:22.798239400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class DuplicateCheckResponse {

  private @Nullable String promptId;

  private @Nullable Boolean hasDuplicates;

  @Valid
  private List<@Valid SearchResponse> duplicates = new ArrayList<>();

  public DuplicateCheckResponse promptId(@Nullable String promptId) {
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

  public DuplicateCheckResponse hasDuplicates(@Nullable Boolean hasDuplicates) {
    this.hasDuplicates = hasDuplicates;
    return this;
  }

  /**
   * Get hasDuplicates
   * @return hasDuplicates
   */
  
  @Schema(name = "hasDuplicates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasDuplicates")
  public @Nullable Boolean getHasDuplicates() {
    return hasDuplicates;
  }

  @JsonProperty("hasDuplicates")
  public void setHasDuplicates(@Nullable Boolean hasDuplicates) {
    this.hasDuplicates = hasDuplicates;
  }

  public DuplicateCheckResponse duplicates(List<@Valid SearchResponse> duplicates) {
    this.duplicates = duplicates;
    return this;
  }

  public DuplicateCheckResponse addDuplicatesItem(SearchResponse duplicatesItem) {
    if (this.duplicates == null) {
      this.duplicates = new ArrayList<>();
    }
    this.duplicates.add(duplicatesItem);
    return this;
  }

  /**
   * Get duplicates
   * @return duplicates
   */
  @Valid 
  @Schema(name = "duplicates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("duplicates")
  public List<@Valid SearchResponse> getDuplicates() {
    return duplicates;
  }

  @JsonProperty("duplicates")
  public void setDuplicates(List<@Valid SearchResponse> duplicates) {
    this.duplicates = duplicates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DuplicateCheckResponse duplicateCheckResponse = (DuplicateCheckResponse) o;
    return Objects.equals(this.promptId, duplicateCheckResponse.promptId) &&
        Objects.equals(this.hasDuplicates, duplicateCheckResponse.hasDuplicates) &&
        Objects.equals(this.duplicates, duplicateCheckResponse.duplicates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptId, hasDuplicates, duplicates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DuplicateCheckResponse {\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    hasDuplicates: ").append(toIndentedString(hasDuplicates)).append("\n");
    sb.append("    duplicates: ").append(toIndentedString(duplicates)).append("\n");
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

