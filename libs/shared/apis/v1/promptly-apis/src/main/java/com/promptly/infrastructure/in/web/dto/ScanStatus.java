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
 * Overall scan verdict
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-25T18:29:22.472644400-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public enum ScanStatus {
  
  PASS("PASS"),
  
  WARN("WARN"),
  
  FAIL("FAIL");

  private final String value;

  ScanStatus(String value) {
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
  public static ScanStatus fromValue(String value) {
    for (ScanStatus b : ScanStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

