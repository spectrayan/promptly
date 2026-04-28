package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Lifecycle status of a prompt
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T16:27:22.798239400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public enum PromptStatus {
  
  DRAFT("DRAFT"),
  
  IN_REVIEW("IN_REVIEW"),
  
  APPROVED("APPROVED"),
  
  REJECTED("REJECTED"),
  
  DEPRECATED("DEPRECATED");

  private final String value;

  PromptStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PromptStatus fromValue(String value) {
    for (PromptStatus b : PromptStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

