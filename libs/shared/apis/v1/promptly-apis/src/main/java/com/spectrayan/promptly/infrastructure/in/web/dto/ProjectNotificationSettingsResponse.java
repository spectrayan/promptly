package com.spectrayan.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.spectrayan.promptly.infrastructure.in.web.dto.NotificationEventTypeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProjectNotificationSettingsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ProjectNotificationSettingsResponse {

  private @Nullable String projectId;

  @Valid
  private List<String> enabledEvents = new ArrayList<>();

  @Valid
  private List<@Valid NotificationEventTypeInfo> availableEvents = new ArrayList<>();

  public ProjectNotificationSettingsResponse projectId(@Nullable String projectId) {
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

  public ProjectNotificationSettingsResponse enabledEvents(List<String> enabledEvents) {
    this.enabledEvents = enabledEvents;
    return this;
  }

  public ProjectNotificationSettingsResponse addEnabledEventsItem(String enabledEventsItem) {
    if (this.enabledEvents == null) {
      this.enabledEvents = new ArrayList<>();
    }
    this.enabledEvents.add(enabledEventsItem);
    return this;
  }

  /**
   * Event type keys enabled for this project
   * @return enabledEvents
   */
  
  @Schema(name = "enabledEvents", description = "Event type keys enabled for this project", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("enabledEvents")
  public List<String> getEnabledEvents() {
    return enabledEvents;
  }

  @JsonProperty("enabledEvents")
  public void setEnabledEvents(List<String> enabledEvents) {
    this.enabledEvents = enabledEvents;
  }

  public ProjectNotificationSettingsResponse availableEvents(List<@Valid NotificationEventTypeInfo> availableEvents) {
    this.availableEvents = availableEvents;
    return this;
  }

  public ProjectNotificationSettingsResponse addAvailableEventsItem(NotificationEventTypeInfo availableEventsItem) {
    if (this.availableEvents == null) {
      this.availableEvents = new ArrayList<>();
    }
    this.availableEvents.add(availableEventsItem);
    return this;
  }

  /**
   * All available notification event types
   * @return availableEvents
   */
  @Valid 
  @Schema(name = "availableEvents", description = "All available notification event types", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availableEvents")
  public List<@Valid NotificationEventTypeInfo> getAvailableEvents() {
    return availableEvents;
  }

  @JsonProperty("availableEvents")
  public void setAvailableEvents(List<@Valid NotificationEventTypeInfo> availableEvents) {
    this.availableEvents = availableEvents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectNotificationSettingsResponse projectNotificationSettingsResponse = (ProjectNotificationSettingsResponse) o;
    return Objects.equals(this.projectId, projectNotificationSettingsResponse.projectId) &&
        Objects.equals(this.enabledEvents, projectNotificationSettingsResponse.enabledEvents) &&
        Objects.equals(this.availableEvents, projectNotificationSettingsResponse.availableEvents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, enabledEvents, availableEvents);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProjectNotificationSettingsResponse {\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    enabledEvents: ").append(toIndentedString(enabledEvents)).append("\n");
    sb.append("    availableEvents: ").append(toIndentedString(availableEvents)).append("\n");
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

