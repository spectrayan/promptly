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
 * NotificationCountResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-29T16:30:34.655679900-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class NotificationCountResponse {

  private @Nullable Long unread;

  public NotificationCountResponse unread(@Nullable Long unread) {
    this.unread = unread;
    return this;
  }

  /**
   * Number of unread notifications for the user in the project
   * @return unread
   */
  
  @Schema(name = "unread", description = "Number of unread notifications for the user in the project", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unread")
  public @Nullable Long getUnread() {
    return unread;
  }

  @JsonProperty("unread")
  public void setUnread(@Nullable Long unread) {
    this.unread = unread;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationCountResponse notificationCountResponse = (NotificationCountResponse) o;
    return Objects.equals(this.unread, notificationCountResponse.unread);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unread);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationCountResponse {\n");
    sb.append("    unread: ").append(toIndentedString(unread)).append("\n");
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

