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
 * Category of vulnerability finding
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-05T23:45:25.362554800-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public enum FindingType {
  
  PHI_EXPOSURE("PHI_EXPOSURE"),
  
  INJECTION_RISK("INJECTION_RISK"),
  
  MISSING_GUARDRAIL("MISSING_GUARDRAIL"),
  
  HALLUCINATION_PRONE("HALLUCINATION_PRONE"),
  
  WEAK_TOOL_CALLING("WEAK_TOOL_CALLING"),

  JAILBREAK_VULNERABLE("JAILBREAK_VULNERABLE"),

  DATA_EXFILTRATION("DATA_EXFILTRATION"),

  PRIVILEGE_ESCALATION("PRIVILEGE_ESCALATION"),

  SYSTEM_PROMPT_LEAK("SYSTEM_PROMPT_LEAK"),

  OUTPUT_MANIPULATION("OUTPUT_MANIPULATION"),

  ENCODING_ATTACK("ENCODING_ATTACK"),

  CONTEXT_POISONING("CONTEXT_POISONING"),

  INSECURE_DEFAULT("INSECURE_DEFAULT"),

  HARMFUL_CONTENT("HARMFUL_CONTENT"),

  REGULATORY_VIOLATION("REGULATORY_VIOLATION"),

  RESOURCE_ABUSE("RESOURCE_ABUSE");

  private final String value;

  FindingType(String value) {
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
  public static FindingType fromValue(String value) {
    for (FindingType b : FindingType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

