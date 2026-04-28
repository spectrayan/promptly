package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * VersionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T16:27:22.798239400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class VersionResponse {

  private @Nullable Integer versionNumber;

  private @Nullable String content;

  private @Nullable String changeMessage;

  private @Nullable String createdBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  public VersionResponse versionNumber(@Nullable Integer versionNumber) {
    this.versionNumber = versionNumber;
    return this;
  }

  /**
   * Get versionNumber
   * @return versionNumber
   */
  
  @Schema(name = "versionNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("versionNumber")
  public @Nullable Integer getVersionNumber() {
    return versionNumber;
  }

  @JsonProperty("versionNumber")
  public void setVersionNumber(@Nullable Integer versionNumber) {
    this.versionNumber = versionNumber;
  }

  public VersionResponse content(@Nullable String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
   */
  
  @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public @Nullable String getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(@Nullable String content) {
    this.content = content;
  }

  public VersionResponse changeMessage(@Nullable String changeMessage) {
    this.changeMessage = changeMessage;
    return this;
  }

  /**
   * Get changeMessage
   * @return changeMessage
   */
  
  @Schema(name = "changeMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("changeMessage")
  public @Nullable String getChangeMessage() {
    return changeMessage;
  }

  @JsonProperty("changeMessage")
  public void setChangeMessage(@Nullable String changeMessage) {
    this.changeMessage = changeMessage;
  }

  public VersionResponse createdBy(@Nullable String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  /**
   * Get createdBy
   * @return createdBy
   */
  
  @Schema(name = "createdBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdBy")
  public @Nullable String getCreatedBy() {
    return createdBy;
  }

  @JsonProperty("createdBy")
  public void setCreatedBy(@Nullable String createdBy) {
    this.createdBy = createdBy;
  }

  public VersionResponse createdAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @Valid 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdAt")
  public @Nullable OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("createdAt")
  public void setCreatedAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VersionResponse versionResponse = (VersionResponse) o;
    return Objects.equals(this.versionNumber, versionResponse.versionNumber) &&
        Objects.equals(this.content, versionResponse.content) &&
        Objects.equals(this.changeMessage, versionResponse.changeMessage) &&
        Objects.equals(this.createdBy, versionResponse.createdBy) &&
        Objects.equals(this.createdAt, versionResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versionNumber, content, changeMessage, createdBy, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VersionResponse {\n");
    sb.append("    versionNumber: ").append(toIndentedString(versionNumber)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    changeMessage: ").append(toIndentedString(changeMessage)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

