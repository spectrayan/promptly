package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AuditResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T15:21:29.885753700-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class AuditResponse {

  private @Nullable String id;

  private @Nullable String action;

  private @Nullable String resourceType;

  private @Nullable String resourceId;

  private @Nullable Integer resourceVersion;

  private @Nullable String actorUserId;

  @Valid
  private Map<String, Object> details = new HashMap<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime timestamp;

  public AuditResponse id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(@Nullable String id) {
    this.id = id;
  }

  public AuditResponse action(@Nullable String action) {
    this.action = action;
    return this;
  }

  /**
   * Event type (e.g., prompt.created, workflow.approved, scan.completed)
   * @return action
   */
  
  @Schema(name = "action", description = "Event type (e.g., prompt.created, workflow.approved, scan.completed)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("action")
  public @Nullable String getAction() {
    return action;
  }

  @JsonProperty("action")
  public void setAction(@Nullable String action) {
    this.action = action;
  }

  public AuditResponse resourceType(@Nullable String resourceType) {
    this.resourceType = resourceType;
    return this;
  }

  /**
   * Resource category (prompt, workflow, scan)
   * @return resourceType
   */
  
  @Schema(name = "resourceType", description = "Resource category (prompt, workflow, scan)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceType")
  public @Nullable String getResourceType() {
    return resourceType;
  }

  @JsonProperty("resourceType")
  public void setResourceType(@Nullable String resourceType) {
    this.resourceType = resourceType;
  }

  public AuditResponse resourceId(@Nullable String resourceId) {
    this.resourceId = resourceId;
    return this;
  }

  /**
   * Get resourceId
   * @return resourceId
   */
  
  @Schema(name = "resourceId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceId")
  public @Nullable String getResourceId() {
    return resourceId;
  }

  @JsonProperty("resourceId")
  public void setResourceId(@Nullable String resourceId) {
    this.resourceId = resourceId;
  }

  public AuditResponse resourceVersion(@Nullable Integer resourceVersion) {
    this.resourceVersion = resourceVersion;
    return this;
  }

  /**
   * Get resourceVersion
   * @return resourceVersion
   */
  
  @Schema(name = "resourceVersion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resourceVersion")
  public @Nullable Integer getResourceVersion() {
    return resourceVersion;
  }

  @JsonProperty("resourceVersion")
  public void setResourceVersion(@Nullable Integer resourceVersion) {
    this.resourceVersion = resourceVersion;
  }

  public AuditResponse actorUserId(@Nullable String actorUserId) {
    this.actorUserId = actorUserId;
    return this;
  }

  /**
   * Get actorUserId
   * @return actorUserId
   */
  
  @Schema(name = "actorUserId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actorUserId")
  public @Nullable String getActorUserId() {
    return actorUserId;
  }

  @JsonProperty("actorUserId")
  public void setActorUserId(@Nullable String actorUserId) {
    this.actorUserId = actorUserId;
  }

  public AuditResponse details(Map<String, Object> details) {
    this.details = details;
    return this;
  }

  public AuditResponse putDetailsItem(String key, Object detailsItem) {
    if (this.details == null) {
      this.details = new HashMap<>();
    }
    this.details.put(key, detailsItem);
    return this;
  }

  /**
   * Contextual event details
   * @return details
   */
  
  @Schema(name = "details", description = "Contextual event details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public Map<String, Object> getDetails() {
    return details;
  }

  @JsonProperty("details")
  public void setDetails(Map<String, Object> details) {
    this.details = details;
  }

  public AuditResponse timestamp(@Nullable OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
   */
  @Valid 
  @Schema(name = "timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestamp")
  public @Nullable OffsetDateTime getTimestamp() {
    return timestamp;
  }

  @JsonProperty("timestamp")
  public void setTimestamp(@Nullable OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuditResponse auditResponse = (AuditResponse) o;
    return Objects.equals(this.id, auditResponse.id) &&
        Objects.equals(this.action, auditResponse.action) &&
        Objects.equals(this.resourceType, auditResponse.resourceType) &&
        Objects.equals(this.resourceId, auditResponse.resourceId) &&
        Objects.equals(this.resourceVersion, auditResponse.resourceVersion) &&
        Objects.equals(this.actorUserId, auditResponse.actorUserId) &&
        Objects.equals(this.details, auditResponse.details) &&
        Objects.equals(this.timestamp, auditResponse.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, action, resourceType, resourceId, resourceVersion, actorUserId, details, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuditResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    resourceType: ").append(toIndentedString(resourceType)).append("\n");
    sb.append("    resourceId: ").append(toIndentedString(resourceId)).append("\n");
    sb.append("    resourceVersion: ").append(toIndentedString(resourceVersion)).append("\n");
    sb.append("    actorUserId: ").append(toIndentedString(actorUserId)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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

