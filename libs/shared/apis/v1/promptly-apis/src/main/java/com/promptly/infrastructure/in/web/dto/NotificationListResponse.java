package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.promptly.infrastructure.in.web.dto.NotificationResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NotificationListResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class NotificationListResponse {

  @Valid
  private List<@Valid NotificationResponse> items = new ArrayList<>();

  private @Nullable Boolean hasMore;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime oldestTimestamp;

  public NotificationListResponse items(List<@Valid NotificationResponse> items) {
    this.items = items;
    return this;
  }

  public NotificationListResponse addItemsItem(NotificationResponse itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
   */
  @Valid 
  @Schema(name = "items", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid NotificationResponse> getItems() {
    return items;
  }

  @JsonProperty("items")
  public void setItems(List<@Valid NotificationResponse> items) {
    this.items = items;
  }

  public NotificationListResponse hasMore(@Nullable Boolean hasMore) {
    this.hasMore = hasMore;
    return this;
  }

  /**
   * Whether older notifications exist beyond this page
   * @return hasMore
   */
  
  @Schema(name = "hasMore", description = "Whether older notifications exist beyond this page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasMore")
  public @Nullable Boolean getHasMore() {
    return hasMore;
  }

  @JsonProperty("hasMore")
  public void setHasMore(@Nullable Boolean hasMore) {
    this.hasMore = hasMore;
  }

  public NotificationListResponse oldestTimestamp(@Nullable OffsetDateTime oldestTimestamp) {
    this.oldestTimestamp = oldestTimestamp;
    return this;
  }

  /**
   * Timestamp of the oldest notification in this page (cursor for next page)
   * @return oldestTimestamp
   */
  @Valid 
  @Schema(name = "oldestTimestamp", description = "Timestamp of the oldest notification in this page (cursor for next page)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("oldestTimestamp")
  public @Nullable OffsetDateTime getOldestTimestamp() {
    return oldestTimestamp;
  }

  @JsonProperty("oldestTimestamp")
  public void setOldestTimestamp(@Nullable OffsetDateTime oldestTimestamp) {
    this.oldestTimestamp = oldestTimestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationListResponse notificationListResponse = (NotificationListResponse) o;
    return Objects.equals(this.items, notificationListResponse.items) &&
        Objects.equals(this.hasMore, notificationListResponse.hasMore) &&
        Objects.equals(this.oldestTimestamp, notificationListResponse.oldestTimestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, hasMore, oldestTimestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationListResponse {\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    hasMore: ").append(toIndentedString(hasMore)).append("\n");
    sb.append("    oldestTimestamp: ").append(toIndentedString(oldestTimestamp)).append("\n");
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

