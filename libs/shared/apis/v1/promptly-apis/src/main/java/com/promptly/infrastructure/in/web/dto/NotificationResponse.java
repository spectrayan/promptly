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
 * NotificationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class NotificationResponse {

  private @Nullable String id;

  private @Nullable String userId;

  private @Nullable String projectId;

  private @Nullable String type;

  private @Nullable String title;

  private @Nullable String message;

  private @Nullable String icon;

  @Valid
  private Map<String, Object> payload = new HashMap<>();

  private @Nullable Boolean read;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  public NotificationResponse id(@Nullable String id) {
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

  public NotificationResponse userId(@Nullable String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public @Nullable String getUserId() {
    return userId;
  }

  @JsonProperty("userId")
  public void setUserId(@Nullable String userId) {
    this.userId = userId;
  }

  public NotificationResponse projectId(@Nullable String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Get projectId
   * @return projectId
   */
  
  @Schema(name = "projectId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("projectId")
  public @Nullable String getProjectId() {
    return projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(@Nullable String projectId) {
    this.projectId = projectId;
  }

  public NotificationResponse type(@Nullable String type) {
    this.type = type;
    return this;
  }

  /**
   * Event type key (e.g., prompt.created, workflow.approved, scan.completed)
   * @return type
   */
  
  @Schema(name = "type", description = "Event type key (e.g., prompt.created, workflow.approved, scan.completed)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public @Nullable String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(@Nullable String type) {
    this.type = type;
  }

  public NotificationResponse title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Human-readable notification title
   * @return title
   */
  
  @Schema(name = "title", description = "Human-readable notification title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  public NotificationResponse message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Human-readable notification message
   * @return message
   */
  
  @Schema(name = "message", description = "Human-readable notification message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  public NotificationResponse icon(@Nullable String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Material icon name for UI rendering
   * @return icon
   */
  
  @Schema(name = "icon", description = "Material icon name for UI rendering", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("icon")
  public @Nullable String getIcon() {
    return icon;
  }

  @JsonProperty("icon")
  public void setIcon(@Nullable String icon) {
    this.icon = icon;
  }

  public NotificationResponse payload(Map<String, Object> payload) {
    this.payload = payload;
    return this;
  }

  public NotificationResponse putPayloadItem(String key, Object payloadItem) {
    if (this.payload == null) {
      this.payload = new HashMap<>();
    }
    this.payload.put(key, payloadItem);
    return this;
  }

  /**
   * Raw event payload for deep-linking or contextual display
   * @return payload
   */
  
  @Schema(name = "payload", description = "Raw event payload for deep-linking or contextual display", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payload")
  public Map<String, Object> getPayload() {
    return payload;
  }

  @JsonProperty("payload")
  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  public NotificationResponse read(@Nullable Boolean read) {
    this.read = read;
    return this;
  }

  /**
   * Whether the notification has been read
   * @return read
   */
  
  @Schema(name = "read", description = "Whether the notification has been read", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("read")
  public @Nullable Boolean getRead() {
    return read;
  }

  @JsonProperty("read")
  public void setRead(@Nullable Boolean read) {
    this.read = read;
  }

  public NotificationResponse createdAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @Valid 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    NotificationResponse notificationResponse = (NotificationResponse) o;
    return Objects.equals(this.id, notificationResponse.id) &&
        Objects.equals(this.userId, notificationResponse.userId) &&
        Objects.equals(this.projectId, notificationResponse.projectId) &&
        Objects.equals(this.type, notificationResponse.type) &&
        Objects.equals(this.title, notificationResponse.title) &&
        Objects.equals(this.message, notificationResponse.message) &&
        Objects.equals(this.icon, notificationResponse.icon) &&
        Objects.equals(this.payload, notificationResponse.payload) &&
        Objects.equals(this.read, notificationResponse.read) &&
        Objects.equals(this.createdAt, notificationResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, projectId, type, title, message, icon, payload, read, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    payload: ").append(toIndentedString(payload)).append("\n");
    sb.append("    read: ").append(toIndentedString(read)).append("\n");
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

