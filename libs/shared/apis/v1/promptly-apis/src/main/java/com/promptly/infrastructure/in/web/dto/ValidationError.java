package com.promptly.infrastructure.in.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Field-level validation error details
 */

@Schema(name = "ValidationError", description = "Field-level validation error details")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-28T15:10:16.805941300-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class ValidationError {

  private String field;

  private String message;

  private @Nullable String rejectedValue;

  private @Nullable String code;

  public ValidationError() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ValidationError(String field, String message) {
    this.field = field;
    this.message = message;
  }

  public ValidationError field(String field) {
    this.field = field;
    return this;
  }

  /**
   * Name of the field that failed validation
   * @return field
   */
  @NotNull 
  @Schema(name = "field", example = "name", description = "Name of the field that failed validation", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("field")
  public String getField() {
    return field;
  }

  @JsonProperty("field")
  public void setField(String field) {
    this.field = field;
  }

  public ValidationError message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Validation error message for this field
   * @return message
   */
  @NotNull 
  @Schema(name = "message", example = "must not be blank", description = "Validation error message for this field", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }

  public ValidationError rejectedValue(@Nullable String rejectedValue) {
    this.rejectedValue = rejectedValue;
    return this;
  }

  /**
   * The value that was rejected (may be null)
   * @return rejectedValue
   */
  
  @Schema(name = "rejectedValue", example = "", description = "The value that was rejected (may be null)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rejectedValue")
  public @Nullable String getRejectedValue() {
    return rejectedValue;
  }

  @JsonProperty("rejectedValue")
  public void setRejectedValue(@Nullable String rejectedValue) {
    this.rejectedValue = rejectedValue;
  }

  public ValidationError code(@Nullable String code) {
    this.code = code;
    return this;
  }

  /**
   * Machine-readable error code for this field
   * @return code
   */
  
  @Schema(name = "code", example = "REQUIRED_FIELD", description = "Machine-readable error code for this field", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public @Nullable String getCode() {
    return code;
  }

  @JsonProperty("code")
  public void setCode(@Nullable String code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationError validationError = (ValidationError) o;
    return Objects.equals(this.field, validationError.field) &&
        Objects.equals(this.message, validationError.message) &&
        Objects.equals(this.rejectedValue, validationError.rejectedValue) &&
        Objects.equals(this.code, validationError.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, message, rejectedValue, code);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidationError {\n");
    sb.append("    field: ").append(toIndentedString(field)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    rejectedValue: ").append(toIndentedString(rejectedValue)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

