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
 * NotificationPreferenceResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class NotificationPreferenceResponse {

  private @Nullable String userId;

  private @Nullable String projectId;

  @Valid
  private List<String> mutedEvents = new ArrayList<>();

  private @Nullable Boolean inAppEnabled;

  private @Nullable Boolean emailEnabled;

  @Valid
  private List<@Valid NotificationEventTypeInfo> availableEvents = new ArrayList<>();

  public NotificationPreferenceResponse userId(@Nullable String userId) {
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

  public NotificationPreferenceResponse projectId(@Nullable String projectId) {
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

  public NotificationPreferenceResponse mutedEvents(List<String> mutedEvents) {
    this.mutedEvents = mutedEvents;
    return this;
  }

  public NotificationPreferenceResponse addMutedEventsItem(String mutedEventsItem) {
    if (this.mutedEvents == null) {
      this.mutedEvents = new ArrayList<>();
    }
    this.mutedEvents.add(mutedEventsItem);
    return this;
  }

  /**
   * Event type keys that the user has muted
   * @return mutedEvents
   */
  
  @Schema(name = "mutedEvents", description = "Event type keys that the user has muted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mutedEvents")
  public List<String> getMutedEvents() {
    return mutedEvents;
  }

  @JsonProperty("mutedEvents")
  public void setMutedEvents(List<String> mutedEvents) {
    this.mutedEvents = mutedEvents;
  }

  public NotificationPreferenceResponse inAppEnabled(@Nullable Boolean inAppEnabled) {
    this.inAppEnabled = inAppEnabled;
    return this;
  }

  /**
   * Whether in-app notifications are enabled
   * @return inAppEnabled
   */
  
  @Schema(name = "inAppEnabled", description = "Whether in-app notifications are enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inAppEnabled")
  public @Nullable Boolean getInAppEnabled() {
    return inAppEnabled;
  }

  @JsonProperty("inAppEnabled")
  public void setInAppEnabled(@Nullable Boolean inAppEnabled) {
    this.inAppEnabled = inAppEnabled;
  }

  public NotificationPreferenceResponse emailEnabled(@Nullable Boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
    return this;
  }

  /**
   * Whether email notifications are enabled
   * @return emailEnabled
   */
  
  @Schema(name = "emailEnabled", description = "Whether email notifications are enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailEnabled")
  public @Nullable Boolean getEmailEnabled() {
    return emailEnabled;
  }

  @JsonProperty("emailEnabled")
  public void setEmailEnabled(@Nullable Boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
  }

  public NotificationPreferenceResponse availableEvents(List<@Valid NotificationEventTypeInfo> availableEvents) {
    this.availableEvents = availableEvents;
    return this;
  }

  public NotificationPreferenceResponse addAvailableEventsItem(NotificationEventTypeInfo availableEventsItem) {
    if (this.availableEvents == null) {
      this.availableEvents = new ArrayList<>();
    }
    this.availableEvents.add(availableEventsItem);
    return this;
  }

  /**
   * All available notification event types for the UI toggle grid
   * @return availableEvents
   */
  @Valid 
  @Schema(name = "availableEvents", description = "All available notification event types for the UI toggle grid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    NotificationPreferenceResponse notificationPreferenceResponse = (NotificationPreferenceResponse) o;
    return Objects.equals(this.userId, notificationPreferenceResponse.userId) &&
        Objects.equals(this.projectId, notificationPreferenceResponse.projectId) &&
        Objects.equals(this.mutedEvents, notificationPreferenceResponse.mutedEvents) &&
        Objects.equals(this.inAppEnabled, notificationPreferenceResponse.inAppEnabled) &&
        Objects.equals(this.emailEnabled, notificationPreferenceResponse.emailEnabled) &&
        Objects.equals(this.availableEvents, notificationPreferenceResponse.availableEvents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, projectId, mutedEvents, inAppEnabled, emailEnabled, availableEvents);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationPreferenceResponse {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    mutedEvents: ").append(toIndentedString(mutedEvents)).append("\n");
    sb.append("    inAppEnabled: ").append(toIndentedString(inAppEnabled)).append("\n");
    sb.append("    emailEnabled: ").append(toIndentedString(emailEnabled)).append("\n");
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

