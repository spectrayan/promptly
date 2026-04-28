package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserPreferences
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T09:45:02.890745600-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class UserPreferences {

  private Boolean inAppEnabled = true;

  private Boolean emailEnabled = true;

  @Valid
  private List<String> mutedEvents = new ArrayList<>();

  public UserPreferences() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserPreferences(Boolean inAppEnabled, Boolean emailEnabled) {
    this.inAppEnabled = inAppEnabled;
    this.emailEnabled = emailEnabled;
  }

  public UserPreferences inAppEnabled(Boolean inAppEnabled) {
    this.inAppEnabled = inAppEnabled;
    return this;
  }

  /**
   * Master switch for in-app (bell icon) notifications
   * @return inAppEnabled
   */
  @NotNull 
  @Schema(name = "inAppEnabled", description = "Master switch for in-app (bell icon) notifications", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("inAppEnabled")
  public Boolean getInAppEnabled() {
    return inAppEnabled;
  }

  @JsonProperty("inAppEnabled")
  public void setInAppEnabled(Boolean inAppEnabled) {
    this.inAppEnabled = inAppEnabled;
  }

  public UserPreferences emailEnabled(Boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
    return this;
  }

  /**
   * Master switch for email notifications
   * @return emailEnabled
   */
  @NotNull 
  @Schema(name = "emailEnabled", description = "Master switch for email notifications", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("emailEnabled")
  public Boolean getEmailEnabled() {
    return emailEnabled;
  }

  @JsonProperty("emailEnabled")
  public void setEmailEnabled(Boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
  }

  public UserPreferences mutedEvents(List<String> mutedEvents) {
    this.mutedEvents = mutedEvents;
    return this;
  }

  public UserPreferences addMutedEventsItem(String mutedEventsItem) {
    if (this.mutedEvents == null) {
      this.mutedEvents = new ArrayList<>();
    }
    this.mutedEvents.add(mutedEventsItem);
    return this;
  }

  /**
   * List of event types the user has silenced
   * @return mutedEvents
   */
  
  @Schema(name = "mutedEvents", example = "[prompt.created, workflow.approved]", description = "List of event types the user has silenced", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mutedEvents")
  public List<String> getMutedEvents() {
    return mutedEvents;
  }

  @JsonProperty("mutedEvents")
  public void setMutedEvents(List<String> mutedEvents) {
    this.mutedEvents = mutedEvents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPreferences userPreferences = (UserPreferences) o;
    return Objects.equals(this.inAppEnabled, userPreferences.inAppEnabled) &&
        Objects.equals(this.emailEnabled, userPreferences.emailEnabled) &&
        Objects.equals(this.mutedEvents, userPreferences.mutedEvents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inAppEnabled, emailEnabled, mutedEvents);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserPreferences {\n");
    sb.append("    inAppEnabled: ").append(toIndentedString(inAppEnabled)).append("\n");
    sb.append("    emailEnabled: ").append(toIndentedString(emailEnabled)).append("\n");
    sb.append("    mutedEvents: ").append(toIndentedString(mutedEvents)).append("\n");
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

