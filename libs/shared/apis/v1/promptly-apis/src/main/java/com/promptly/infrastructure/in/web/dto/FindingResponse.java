package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.FindingType;
import com.promptly.infrastructure.in.web.dto.Severity;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * FindingResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T15:10:16.805941300-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class FindingResponse {

  private @Nullable FindingType type;

  private @Nullable Severity severity;

  private @Nullable String title;

  private @Nullable String description;

  private @Nullable String remediation;

  public FindingResponse type(@Nullable FindingType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  @Valid 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public @Nullable FindingType getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(@Nullable FindingType type) {
    this.type = type;
  }

  public FindingResponse severity(@Nullable Severity severity) {
    this.severity = severity;
    return this;
  }

  /**
   * Get severity
   * @return severity
   */
  @Valid 
  @Schema(name = "severity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("severity")
  public @Nullable Severity getSeverity() {
    return severity;
  }

  @JsonProperty("severity")
  public void setSeverity(@Nullable Severity severity) {
    this.severity = severity;
  }

  public FindingResponse title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  public FindingResponse description(@Nullable String description) {
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

  public FindingResponse remediation(@Nullable String remediation) {
    this.remediation = remediation;
    return this;
  }

  /**
   * Suggested fix
   * @return remediation
   */
  
  @Schema(name = "remediation", description = "Suggested fix", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("remediation")
  public @Nullable String getRemediation() {
    return remediation;
  }

  @JsonProperty("remediation")
  public void setRemediation(@Nullable String remediation) {
    this.remediation = remediation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FindingResponse findingResponse = (FindingResponse) o;
    return Objects.equals(this.type, findingResponse.type) &&
        Objects.equals(this.severity, findingResponse.severity) &&
        Objects.equals(this.title, findingResponse.title) &&
        Objects.equals(this.description, findingResponse.description) &&
        Objects.equals(this.remediation, findingResponse.remediation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, severity, title, description, remediation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FindingResponse {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    remediation: ").append(toIndentedString(remediation)).append("\n");
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

