package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.OrgRole;
import com.promptly.infrastructure.in.web.dto.UserStatus;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-25T03:57:14.880945462-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class UserResponse {

  private String id;

  private String email;

  private String displayName;

  private @Nullable String avatarUrl;

  private OrgRole orgRole;

  private UserStatus status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  public UserResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserResponse(String id, String email, String displayName, OrgRole orgRole, UserStatus status) {
    this.id = id;
    this.email = email;
    this.displayName = displayName;
    this.orgRole = orgRole;
    this.status = status;
  }

  public UserResponse id(String id) {
    this.id = id;
    return this;
  }

  /**
   * User's unique identifier
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "usr-001", description = "User's unique identifier", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public UserResponse email(String email) {
    this.email = email;
    return this;
  }

  /**
   * User's email address
   * @return email
   */
  @NotNull @jakarta.validation.constraints.Email 
  @Schema(name = "email", example = "alice@promptly.ai", description = "User's email address", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  @JsonProperty("email")
  public void setEmail(String email) {
    this.email = email;
  }

  public UserResponse displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * User's display name
   * @return displayName
   */
  @NotNull 
  @Schema(name = "displayName", example = "Alice Johnson", description = "User's display name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public UserResponse avatarUrl(@Nullable String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * URL to user's avatar image
   * @return avatarUrl
   */
  
  @Schema(name = "avatarUrl", description = "URL to user's avatar image", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avatarUrl")
  public @Nullable String getAvatarUrl() {
    return avatarUrl;
  }

  @JsonProperty("avatarUrl")
  public void setAvatarUrl(@Nullable String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public UserResponse orgRole(OrgRole orgRole) {
    this.orgRole = orgRole;
    return this;
  }

  /**
   * Get orgRole
   * @return orgRole
   */
  @NotNull @Valid 
  @Schema(name = "orgRole", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("orgRole")
  public OrgRole getOrgRole() {
    return orgRole;
  }

  @JsonProperty("orgRole")
  public void setOrgRole(OrgRole orgRole) {
    this.orgRole = orgRole;
  }

  public UserResponse status(UserStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public UserStatus getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public UserResponse createdAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Account creation timestamp
   * @return createdAt
   */
  @Valid 
  @Schema(name = "createdAt", example = "2025-11-01T08:00:00Z", description = "Account creation timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    UserResponse userResponse = (UserResponse) o;
    return Objects.equals(this.id, userResponse.id) &&
        Objects.equals(this.email, userResponse.email) &&
        Objects.equals(this.displayName, userResponse.displayName) &&
        Objects.equals(this.avatarUrl, userResponse.avatarUrl) &&
        Objects.equals(this.orgRole, userResponse.orgRole) &&
        Objects.equals(this.status, userResponse.status) &&
        Objects.equals(this.createdAt, userResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, displayName, avatarUrl, orgRole, status, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    orgRole: ").append(toIndentedString(orgRole)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

