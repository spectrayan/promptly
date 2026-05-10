package com.spectrayan.promptly.infrastructure.in.web.dto;

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
 * UpdateNotificationPreferenceRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class UpdateNotificationPreferenceRequest {

  private String projectId;

  @Valid
  private List<String> mutedEvents = new ArrayList<>();

  private Boolean inAppEnabled = true;

  private Boolean emailEnabled = true;

  public UpdateNotificationPreferenceRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateNotificationPreferenceRequest(String projectId) {
    this.projectId = projectId;
  }

  public UpdateNotificationPreferenceRequest projectId(String projectId) {
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

  public UpdateNotificationPreferenceRequest mutedEvents(List<String> mutedEvents) {
    this.mutedEvents = mutedEvents;
    return this;
  }

  public UpdateNotificationPreferenceRequest addMutedEventsItem(String mutedEventsItem) {
    if (this.mutedEvents == null) {
      this.mutedEvents = new ArrayList<>();
    }
    this.mutedEvents.add(mutedEventsItem);
    return this;
  }

  /**
   * Event type keys to mute
   * @return mutedEvents
   */
  
  @Schema(name = "mutedEvents", description = "Event type keys to mute", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mutedEvents")
  public List<String> getMutedEvents() {
    return mutedEvents;
  }

  @JsonProperty("mutedEvents")
  public void setMutedEvents(List<String> mutedEvents) {
    this.mutedEvents = mutedEvents;
  }

  public UpdateNotificationPreferenceRequest inAppEnabled(Boolean inAppEnabled) {
    this.inAppEnabled = inAppEnabled;
    return this;
  }

  /**
   * Get inAppEnabled
   * @return inAppEnabled
   */
  
  @Schema(name = "inAppEnabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inAppEnabled")
  public Boolean getInAppEnabled() {
    return inAppEnabled;
  }

  @JsonProperty("inAppEnabled")
  public void setInAppEnabled(Boolean inAppEnabled) {
    this.inAppEnabled = inAppEnabled;
  }

  public UpdateNotificationPreferenceRequest emailEnabled(Boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
    return this;
  }

  /**
   * Get emailEnabled
   * @return emailEnabled
   */
  
  @Schema(name = "emailEnabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailEnabled")
  public Boolean getEmailEnabled() {
    return emailEnabled;
  }

  @JsonProperty("emailEnabled")
  public void setEmailEnabled(Boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateNotificationPreferenceRequest updateNotificationPreferenceRequest = (UpdateNotificationPreferenceRequest) o;
    return Objects.equals(this.projectId, updateNotificationPreferenceRequest.projectId) &&
        Objects.equals(this.mutedEvents, updateNotificationPreferenceRequest.mutedEvents) &&
        Objects.equals(this.inAppEnabled, updateNotificationPreferenceRequest.inAppEnabled) &&
        Objects.equals(this.emailEnabled, updateNotificationPreferenceRequest.emailEnabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, mutedEvents, inAppEnabled, emailEnabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateNotificationPreferenceRequest {\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    mutedEvents: ").append(toIndentedString(mutedEvents)).append("\n");
    sb.append("    inAppEnabled: ").append(toIndentedString(inAppEnabled)).append("\n");
    sb.append("    emailEnabled: ").append(toIndentedString(emailEnabled)).append("\n");
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

