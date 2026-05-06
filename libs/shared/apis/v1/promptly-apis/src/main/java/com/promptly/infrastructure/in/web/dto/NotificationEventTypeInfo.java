package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Metadata for a notification event type used in the UI toggle grid
 */

@Schema(name = "NotificationEventTypeInfo", description = "Metadata for a notification event type used in the UI toggle grid")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class NotificationEventTypeInfo {

  private @Nullable String key;

  private @Nullable String title;

  private @Nullable String icon;

  private @Nullable String messageTemplate;

  public NotificationEventTypeInfo key(@Nullable String key) {
    this.key = key;
    return this;
  }

  /**
   * Event type key (e.g., prompt.created, workflow.approved)
   * @return key
   */
  
  @Schema(name = "key", description = "Event type key (e.g., prompt.created, workflow.approved)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("key")
  public @Nullable String getKey() {
    return key;
  }

  @JsonProperty("key")
  public void setKey(@Nullable String key) {
    this.key = key;
  }

  public NotificationEventTypeInfo title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Human-readable display title
   * @return title
   */
  
  @Schema(name = "title", description = "Human-readable display title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  public NotificationEventTypeInfo icon(@Nullable String icon) {
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

  public NotificationEventTypeInfo messageTemplate(@Nullable String messageTemplate) {
    this.messageTemplate = messageTemplate;
    return this;
  }

  /**
   * Message template with {placeholder} variables
   * @return messageTemplate
   */
  
  @Schema(name = "messageTemplate", description = "Message template with {placeholder} variables", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messageTemplate")
  public @Nullable String getMessageTemplate() {
    return messageTemplate;
  }

  @JsonProperty("messageTemplate")
  public void setMessageTemplate(@Nullable String messageTemplate) {
    this.messageTemplate = messageTemplate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationEventTypeInfo notificationEventTypeInfo = (NotificationEventTypeInfo) o;
    return Objects.equals(this.key, notificationEventTypeInfo.key) &&
        Objects.equals(this.title, notificationEventTypeInfo.title) &&
        Objects.equals(this.icon, notificationEventTypeInfo.icon) &&
        Objects.equals(this.messageTemplate, notificationEventTypeInfo.messageTemplate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, title, icon, messageTemplate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationEventTypeInfo {\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    messageTemplate: ").append(toIndentedString(messageTemplate)).append("\n");
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

