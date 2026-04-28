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
 * DeliveryResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T15:10:16.805941300-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class DeliveryResponse {

  private @Nullable String promptId;

  private @Nullable String name;

  private @Nullable String content;

  private @Nullable Integer version;

  private @Nullable String contentFormat;

  public DeliveryResponse promptId(@Nullable String promptId) {
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

  public DeliveryResponse name(@Nullable String name) {
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

  public DeliveryResponse content(@Nullable String content) {
    this.content = content;
    return this;
  }

  /**
   * The full prompt content for runtime use
   * @return content
   */
  
  @Schema(name = "content", description = "The full prompt content for runtime use", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public @Nullable String getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(@Nullable String content) {
    this.content = content;
  }

  public DeliveryResponse version(@Nullable Integer version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
   */
  
  @Schema(name = "version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public @Nullable Integer getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(@Nullable Integer version) {
    this.version = version;
  }

  public DeliveryResponse contentFormat(@Nullable String contentFormat) {
    this.contentFormat = contentFormat;
    return this;
  }

  /**
   * Get contentFormat
   * @return contentFormat
   */
  
  @Schema(name = "contentFormat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contentFormat")
  public @Nullable String getContentFormat() {
    return contentFormat;
  }

  @JsonProperty("contentFormat")
  public void setContentFormat(@Nullable String contentFormat) {
    this.contentFormat = contentFormat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryResponse deliveryResponse = (DeliveryResponse) o;
    return Objects.equals(this.promptId, deliveryResponse.promptId) &&
        Objects.equals(this.name, deliveryResponse.name) &&
        Objects.equals(this.content, deliveryResponse.content) &&
        Objects.equals(this.version, deliveryResponse.version) &&
        Objects.equals(this.contentFormat, deliveryResponse.contentFormat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptId, name, content, version, contentFormat);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryResponse {\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    contentFormat: ").append(toIndentedString(contentFormat)).append("\n");
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

