package com.spectrayan.promptly.infrastructure.in.web.dto;

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
 * ApiKeyCreatedResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.21.0")
public class ApiKeyCreatedResponse {

  private String id;

  private String projectId;

  private String name;

  private String prefix;

  private String apiKey;

  private Boolean revoked;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime expiresAt;

  private @Nullable String createdBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public ApiKeyCreatedResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ApiKeyCreatedResponse(String id, String projectId, String name, String prefix, String apiKey, Boolean revoked, OffsetDateTime createdAt) {
    this.id = id;
    this.projectId = projectId;
    this.name = name;
    this.prefix = prefix;
    this.apiKey = apiKey;
    this.revoked = revoked;
    this.createdAt = createdAt;
  }

  public ApiKeyCreatedResponse id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "08D2X0PKM6C00", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public ApiKeyCreatedResponse projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Get projectId
   * @return projectId
   */
  @NotNull 
  @Schema(name = "projectId", example = "proj-001", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("projectId")
  public String getProjectId() {
    return projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public ApiKeyCreatedResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "production-agent", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public ApiKeyCreatedResponse prefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * Public prefix used for identifying the key
   * @return prefix
   */
  @NotNull 
  @Schema(name = "prefix", example = "prk_live_a1b2c3", description = "Public prefix used for identifying the key", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("prefix")
  public String getPrefix() {
    return prefix;
  }

  @JsonProperty("prefix")
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public ApiKeyCreatedResponse apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  /**
   * Full raw API key. Returned only once upon generation.
   * @return apiKey
   */
  @NotNull 
  @Schema(name = "apiKey", example = "prk_live_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6", description = "Full raw API key. Returned only once upon generation.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("apiKey")
  public String getApiKey() {
    return apiKey;
  }

  @JsonProperty("apiKey")
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public ApiKeyCreatedResponse revoked(Boolean revoked) {
    this.revoked = revoked;
    return this;
  }

  /**
   * Get revoked
   * @return revoked
   */
  @NotNull 
  @Schema(name = "revoked", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("revoked")
  public Boolean getRevoked() {
    return revoked;
  }

  @JsonProperty("revoked")
  public void setRevoked(Boolean revoked) {
    this.revoked = revoked;
  }

  public ApiKeyCreatedResponse expiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  /**
   * Get expiresAt
   * @return expiresAt
   */
  @Valid 
  @Schema(name = "expiresAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiresAt")
  public @Nullable OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  @JsonProperty("expiresAt")
  public void setExpiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public ApiKeyCreatedResponse createdBy(@Nullable String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  /**
   * Get createdBy
   * @return createdBy
   */
  
  @Schema(name = "createdBy", example = "usr-001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdBy")
  public @Nullable String getCreatedBy() {
    return createdBy;
  }

  @JsonProperty("createdBy")
  public void setCreatedBy(@Nullable String createdBy) {
    this.createdBy = createdBy;
  }

  public ApiKeyCreatedResponse createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2026-09-15T00:00:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("createdAt")
  public void setCreatedAt(OffsetDateTime createdAt) {
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
    ApiKeyCreatedResponse apiKeyCreatedResponse = (ApiKeyCreatedResponse) o;
    return Objects.equals(this.id, apiKeyCreatedResponse.id) &&
        Objects.equals(this.projectId, apiKeyCreatedResponse.projectId) &&
        Objects.equals(this.name, apiKeyCreatedResponse.name) &&
        Objects.equals(this.prefix, apiKeyCreatedResponse.prefix) &&
        Objects.equals(this.apiKey, apiKeyCreatedResponse.apiKey) &&
        Objects.equals(this.revoked, apiKeyCreatedResponse.revoked) &&
        Objects.equals(this.expiresAt, apiKeyCreatedResponse.expiresAt) &&
        Objects.equals(this.createdBy, apiKeyCreatedResponse.createdBy) &&
        Objects.equals(this.createdAt, apiKeyCreatedResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, projectId, name, prefix, apiKey, revoked, expiresAt, createdBy, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiKeyCreatedResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    prefix: ").append(toIndentedString(prefix)).append("\n");
    sb.append("    apiKey: ").append(toIndentedString(apiKey)).append("\n");
    sb.append("    revoked: ").append(toIndentedString(revoked)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
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

