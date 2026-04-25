package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.ProjectRole;
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
 * ProjectMemberResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-25T18:29:22.472644400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ProjectMemberResponse {

  private String userId;

  private String displayName;

  private String email;

  private ProjectRole role;

  private @Nullable String addedBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime addedAt;

  public ProjectMemberResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProjectMemberResponse(String userId, String displayName, String email, ProjectRole role) {
    this.userId = userId;
    this.displayName = displayName;
    this.email = email;
    this.role = role;
  }

  public ProjectMemberResponse userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  @NotNull 
  @Schema(name = "userId", example = "usr-002", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  @JsonProperty("userId")
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public ProjectMemberResponse displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
   */
  @NotNull 
  @Schema(name = "displayName", example = "Bob Chen", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ProjectMemberResponse email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  @NotNull 
  @Schema(name = "email", example = "bob@promptly.ai", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  @JsonProperty("email")
  public void setEmail(String email) {
    this.email = email;
  }

  public ProjectMemberResponse role(ProjectRole role) {
    this.role = role;
    return this;
  }

  /**
   * Get role
   * @return role
   */
  @NotNull @Valid 
  @Schema(name = "role", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("role")
  public ProjectRole getRole() {
    return role;
  }

  @JsonProperty("role")
  public void setRole(ProjectRole role) {
    this.role = role;
  }

  public ProjectMemberResponse addedBy(@Nullable String addedBy) {
    this.addedBy = addedBy;
    return this;
  }

  /**
   * Get addedBy
   * @return addedBy
   */
  
  @Schema(name = "addedBy", example = "usr-001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addedBy")
  public @Nullable String getAddedBy() {
    return addedBy;
  }

  @JsonProperty("addedBy")
  public void setAddedBy(@Nullable String addedBy) {
    this.addedBy = addedBy;
  }

  public ProjectMemberResponse addedAt(@Nullable OffsetDateTime addedAt) {
    this.addedAt = addedAt;
    return this;
  }

  /**
   * Get addedAt
   * @return addedAt
   */
  @Valid 
  @Schema(name = "addedAt", example = "2025-07-15T10:00:00Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addedAt")
  public @Nullable OffsetDateTime getAddedAt() {
    return addedAt;
  }

  @JsonProperty("addedAt")
  public void setAddedAt(@Nullable OffsetDateTime addedAt) {
    this.addedAt = addedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectMemberResponse projectMemberResponse = (ProjectMemberResponse) o;
    return Objects.equals(this.userId, projectMemberResponse.userId) &&
        Objects.equals(this.displayName, projectMemberResponse.displayName) &&
        Objects.equals(this.email, projectMemberResponse.email) &&
        Objects.equals(this.role, projectMemberResponse.role) &&
        Objects.equals(this.addedBy, projectMemberResponse.addedBy) &&
        Objects.equals(this.addedAt, projectMemberResponse.addedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, displayName, email, role, addedBy, addedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProjectMemberResponse {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    addedBy: ").append(toIndentedString(addedBy)).append("\n");
    sb.append("    addedAt: ").append(toIndentedString(addedAt)).append("\n");
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

