package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * UpdateProjectNotificationSettingsRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-29T16:30:34.655679900-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class UpdateProjectNotificationSettingsRequest {

  private String projectId;

  @Valid
  private List<String> enabledEvents = new ArrayList<>();

  public UpdateProjectNotificationSettingsRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateProjectNotificationSettingsRequest(String projectId) {
    this.projectId = projectId;
  }

  public UpdateProjectNotificationSettingsRequest projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Get projectId
   * @return projectId
   */
  @NotNull 
  @Schema(name = "projectId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("projectId")
  public String getProjectId() {
    return projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public UpdateProjectNotificationSettingsRequest enabledEvents(List<String> enabledEvents) {
    this.enabledEvents = enabledEvents;
    return this;
  }

  public UpdateProjectNotificationSettingsRequest addEnabledEventsItem(String enabledEventsItem) {
    if (this.enabledEvents == null) {
      this.enabledEvents = new ArrayList<>();
    }
    this.enabledEvents.add(enabledEventsItem);
    return this;
  }

  /**
   * Event type keys to enable for this project
   * @return enabledEvents
   */
  
  @Schema(name = "enabledEvents", description = "Event type keys to enable for this project", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("enabledEvents")
  public List<String> getEnabledEvents() {
    return enabledEvents;
  }

  @JsonProperty("enabledEvents")
  public void setEnabledEvents(List<String> enabledEvents) {
    this.enabledEvents = enabledEvents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateProjectNotificationSettingsRequest updateProjectNotificationSettingsRequest = (UpdateProjectNotificationSettingsRequest) o;
    return Objects.equals(this.projectId, updateProjectNotificationSettingsRequest.projectId) &&
        Objects.equals(this.enabledEvents, updateProjectNotificationSettingsRequest.enabledEvents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, enabledEvents);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateProjectNotificationSettingsRequest {\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    enabledEvents: ").append(toIndentedString(enabledEvents)).append("\n");
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

