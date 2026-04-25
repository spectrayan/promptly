package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateProjectRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-25T03:57:14.880945462-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class CreateProjectRequest {

  private String name;

  private @Nullable String description;

  @Valid
  private List<String> tags = new ArrayList<>();

  public CreateProjectRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateProjectRequest(String name) {
    this.name = name;
  }

  public CreateProjectRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Project name (unique)
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "customer-ops", description = "Project name (unique)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public CreateProjectRequest description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Brief description of the project
   * @return description
   */
  
  @Schema(name = "description", example = "Customer operations AI prompts", description = "Brief description of the project", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public CreateProjectRequest tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public CreateProjectRequest addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Tags for cross-project discovery
   * @return tags
   */
  
  @Schema(name = "tags", example = "[support, nlp]", description = "Tags for cross-project discovery", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }

  @JsonProperty("tags")
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateProjectRequest createProjectRequest = (CreateProjectRequest) o;
    return Objects.equals(this.name, createProjectRequest.name) &&
        Objects.equals(this.description, createProjectRequest.description) &&
        Objects.equals(this.tags, createProjectRequest.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, tags);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateProjectRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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

