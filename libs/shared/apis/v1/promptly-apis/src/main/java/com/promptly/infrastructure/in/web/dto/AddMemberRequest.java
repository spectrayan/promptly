package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.promptly.infrastructure.in.web.dto.ProjectRole;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AddMemberRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T15:21:29.885753700-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class AddMemberRequest {

  private String userId;

  private ProjectRole role;

  public AddMemberRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AddMemberRequest(String userId, ProjectRole role) {
    this.userId = userId;
    this.role = role;
  }

  public AddMemberRequest userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * User ID to add as member
   * @return userId
   */
  @NotNull 
  @Schema(name = "userId", example = "usr-002", description = "User ID to add as member", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  @JsonProperty("userId")
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public AddMemberRequest role(ProjectRole role) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddMemberRequest addMemberRequest = (AddMemberRequest) o;
    return Objects.equals(this.userId, addMemberRequest.userId) &&
        Objects.equals(this.role, addMemberRequest.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddMemberRequest {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
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

