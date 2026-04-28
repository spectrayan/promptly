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
 * ApproveRejectRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T15:10:16.805941300-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ApproveRejectRequest {

  private String actor;

  private @Nullable String comment;

  public ApproveRejectRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ApproveRejectRequest(String actor) {
    this.actor = actor;
  }

  public ApproveRejectRequest actor(String actor) {
    this.actor = actor;
    return this;
  }

  /**
   * User performing the action
   * @return actor
   */
  @NotNull 
  @Schema(name = "actor", description = "User performing the action", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("actor")
  public String getActor() {
    return actor;
  }

  @JsonProperty("actor")
  public void setActor(String actor) {
    this.actor = actor;
  }

  public ApproveRejectRequest comment(@Nullable String comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Optional comment
   * @return comment
   */
  
  @Schema(name = "comment", description = "Optional comment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("comment")
  public @Nullable String getComment() {
    return comment;
  }

  @JsonProperty("comment")
  public void setComment(@Nullable String comment) {
    this.comment = comment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApproveRejectRequest approveRejectRequest = (ApproveRejectRequest) o;
    return Objects.equals(this.actor, approveRejectRequest.actor) &&
        Objects.equals(this.comment, approveRejectRequest.comment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(actor, comment);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApproveRejectRequest {\n");
    sb.append("    actor: ").append(toIndentedString(actor)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
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

