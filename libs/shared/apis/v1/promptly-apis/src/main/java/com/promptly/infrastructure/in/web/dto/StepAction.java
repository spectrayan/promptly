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
 * Action taken on a workflow approval step
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T09:45:02.890745600-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public enum StepAction {
  
  PENDING("PENDING"),
  
  APPROVED("APPROVED"),
  
  REJECTED("REJECTED");

  private final String value;

  StepAction(String value) {
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
  public static StepAction fromValue(String value) {
    for (StepAction b : StepAction.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

