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
 * ApiKeyResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-14T20:09:35.901615400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ApiKeyResponse {

  private String id;

  private String projectId;

  private String name;

  private String prefix;

  private Boolean revoked;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime revokedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime expiresAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime lastUsedAt;

  private @Nullable String createdBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public ApiKeyResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ApiKeyResponse(String id, String projectId, String name, String prefix, Boolean revoked, OffsetDateTime createdAt) {
    this.id = id;
    this.projectId = projectId;
    this.name = name;
    this.prefix = prefix;
    this.revoked = revoked;
    this.createdAt = createdAt;
  }

  public ApiKeyResponse id(String id) {
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

  public ApiKeyResponse projectId(String projectId) {
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

  public ApiKeyResponse name(String name) {
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

  public ApiKeyResponse prefix(String prefix) {
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

  public ApiKeyResponse revoked(Boolean revoked) {
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

  public ApiKeyResponse revokedAt(@Nullable OffsetDateTime revokedAt) {
    this.revokedAt = revokedAt;
    return this;
  }

  /**
   * Get revokedAt
   * @return revokedAt
   */
  @Valid 
  @Schema(name = "revokedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revokedAt")
  public @Nullable OffsetDateTime getRevokedAt() {
    return revokedAt;
  }

  @JsonProperty("revokedAt")
  public void setRevokedAt(@Nullable OffsetDateTime revokedAt) {
    this.revokedAt = revokedAt;
  }

  public ApiKeyResponse expiresAt(@Nullable OffsetDateTime expiresAt) {
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

  public ApiKeyResponse lastUsedAt(@Nullable OffsetDateTime lastUsedAt) {
    this.lastUsedAt = lastUsedAt;
    return this;
  }

  /**
   * Get lastUsedAt
   * @return lastUsedAt
   */
  @Valid 
  @Schema(name = "lastUsedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastUsedAt")
  public @Nullable OffsetDateTime getLastUsedAt() {
    return lastUsedAt;
  }

  @JsonProperty("lastUsedAt")
  public void setLastUsedAt(@Nullable OffsetDateTime lastUsedAt) {
    this.lastUsedAt = lastUsedAt;
  }

  public ApiKeyResponse createdBy(@Nullable String createdBy) {
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

  public ApiKeyResponse createdAt(OffsetDateTime createdAt) {
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
    ApiKeyResponse apiKeyResponse = (ApiKeyResponse) o;
    return Objects.equals(this.id, apiKeyResponse.id) &&
        Objects.equals(this.projectId, apiKeyResponse.projectId) &&
        Objects.equals(this.name, apiKeyResponse.name) &&
        Objects.equals(this.prefix, apiKeyResponse.prefix) &&
        Objects.equals(this.revoked, apiKeyResponse.revoked) &&
        Objects.equals(this.revokedAt, apiKeyResponse.revokedAt) &&
        Objects.equals(this.expiresAt, apiKeyResponse.expiresAt) &&
        Objects.equals(this.lastUsedAt, apiKeyResponse.lastUsedAt) &&
        Objects.equals(this.createdBy, apiKeyResponse.createdBy) &&
        Objects.equals(this.createdAt, apiKeyResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, projectId, name, prefix, revoked, revokedAt, expiresAt, lastUsedAt, createdBy, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiKeyResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    prefix: ").append(toIndentedString(prefix)).append("\n");
    sb.append("    revoked: ").append(toIndentedString(revoked)).append("\n");
    sb.append("    revokedAt: ").append(toIndentedString(revokedAt)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
    sb.append("    lastUsedAt: ").append(toIndentedString(lastUsedAt)).append("\n");
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

