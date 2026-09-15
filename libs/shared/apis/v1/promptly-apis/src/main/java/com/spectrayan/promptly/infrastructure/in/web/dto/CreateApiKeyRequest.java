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
 * CreateApiKeyRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-14T20:09:35.901615400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class CreateApiKeyRequest {

  private String name;

  private @Nullable Integer expiresInDays;

  public CreateApiKeyRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateApiKeyRequest(String name) {
    this.name = name;
  }

  public CreateApiKeyRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Human-readable name/label for the API key
   * @return name
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "name", example = "production-agent", description = "Human-readable name/label for the API key", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public CreateApiKeyRequest expiresInDays(@Nullable Integer expiresInDays) {
    this.expiresInDays = expiresInDays;
    return this;
  }

  /**
   * Optional duration in days until expiration. Omit for a non-expiring key.
   * minimum: 1
   * maximum: 365
   * @return expiresInDays
   */
  @Min(value = 1) @Max(value = 365) 
  @Schema(name = "expiresInDays", example = "90", description = "Optional duration in days until expiration. Omit for a non-expiring key.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiresInDays")
  public @Nullable Integer getExpiresInDays() {
    return expiresInDays;
  }

  @JsonProperty("expiresInDays")
  public void setExpiresInDays(@Nullable Integer expiresInDays) {
    this.expiresInDays = expiresInDays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateApiKeyRequest createApiKeyRequest = (CreateApiKeyRequest) o;
    return Objects.equals(this.name, createApiKeyRequest.name) &&
        Objects.equals(this.expiresInDays, createApiKeyRequest.expiresInDays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, expiresInDays);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateApiKeyRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    expiresInDays: ").append(toIndentedString(expiresInDays)).append("\n");
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

