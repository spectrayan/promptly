package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AddProjectMemberRequest
 */

@JsonTypeName("addProjectMember_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-24T21:29:54.630159449-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class AddProjectMemberRequest {

  private String userId;

  /**
   * Project-level RBAC role
   */
  public enum RoleEnum {
    VIEWER("VIEWER"),
    
    AUTHOR("AUTHOR"),
    
    REVIEWER("REVIEWER"),
    
    APPROVER("APPROVER"),
    
    ADMIN("ADMIN");

    private final String value;

    RoleEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RoleEnum fromValue(String value) {
      for (RoleEnum b : RoleEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private RoleEnum role;

  public AddProjectMemberRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AddProjectMemberRequest(String userId, RoleEnum role) {
    this.userId = userId;
    this.role = role;
  }

  public AddProjectMemberRequest userId(String userId) {
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

  public AddProjectMemberRequest role(RoleEnum role) {
    this.role = role;
    return this;
  }

  /**
   * Project-level RBAC role
   * @return role
   */
  @NotNull 
  @Schema(name = "role", description = "Project-level RBAC role", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("role")
  public RoleEnum getRole() {
    return role;
  }

  @JsonProperty("role")
  public void setRole(RoleEnum role) {
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
    AddProjectMemberRequest addProjectMemberRequest = (AddProjectMemberRequest) o;
    return Objects.equals(this.userId, addProjectMemberRequest.userId) &&
        Objects.equals(this.role, addProjectMemberRequest.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, role);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddProjectMemberRequest {\n");
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

