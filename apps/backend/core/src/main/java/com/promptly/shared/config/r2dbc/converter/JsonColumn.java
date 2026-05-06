package com.promptly.shared.config.r2dbc.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Lightweight wrapper for JSON column values in R2DBC entities.
 * <p>
 * Using this type instead of raw {@code String} allows Spring Data R2DBC custom
 * converters to target JSON columns precisely — without accidentally converting
 * every {@code String} field.
 * <p>
 * Entities declare fields as {@code JsonColumn} and the registered
 * {@link JsonColumnReadingConverter} / {@link JsonColumnWritingConverter}
 * handle the database ↔ Java serialization transparently.
 *
 * <h3>Usage in entities:</h3>
 * <pre>{@code
 * @Column("tags")
 * private JsonColumn tags;
 * }</pre>
 *
 * <h3>Usage in adapters:</h3>
 * <pre>{@code
 * // Writing (domain → entity)
 * .tags(JsonColumn.of(prompt.getTags()))
 *
 * // Reading (entity → domain)
 * Set<String> tags = entity.getTags().toSet();
 * Map<String, Object> payload = entity.getPayload().toMap();
 * }</pre>
 *
 * @see JsonColumnReadingConverter
 * @see JsonColumnWritingConverter
 */
public final class JsonColumn {

    /** Shared ObjectMapper — thread-safe and reusable. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Fallback for null/empty JSON arrays. */
    private static final JsonColumn EMPTY_ARRAY = new JsonColumn("[]");

    /** Fallback for null/empty JSON objects. */
    private static final JsonColumn EMPTY_OBJECT = new JsonColumn("{}");

    private final String json;

    private JsonColumn(String json) {
        this.json = json;
    }

    // ── Factory methods ─────────────────────────────────────────────

    /**
     * Creates a {@code JsonColumn} from a raw JSON string.
     * Used by R2DBC converters when reading from the database.
     */
    public static JsonColumn fromJson(String json) {
        return json != null ? new JsonColumn(json) : null;
    }

    /**
     * Serializes any Java object into a {@code JsonColumn}.
     * <p>
     * Handles {@code null} gracefully — returns {@code null} so the DB column stays NULL.
     */
    public static JsonColumn of(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return new JsonColumn(MAPPER.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize value to JSON", e);
        }
    }

    /**
     * Creates a {@code JsonColumn} with a default empty array ({@code []}).
     * Useful when a non-null default is needed for collection columns.
     */
    public static JsonColumn emptyArray() {
        return EMPTY_ARRAY;
    }

    /**
     * Creates a {@code JsonColumn} with a default empty object ({@code {}}).
     */
    public static JsonColumn emptyObject() {
        return EMPTY_OBJECT;
    }

    /**
     * Creates a {@code JsonColumn} from a value, falling back to an empty array
     * if the value is {@code null}. Convenient for list/set columns that should
     * never be NULL in the database.
     */
    public static JsonColumn ofOrEmptyArray(Object value) {
        return value != null ? of(value) : EMPTY_ARRAY;
    }

    // ── Deserialization methods ──────────────────────────────────────

    /**
     * Deserializes the JSON into a {@code Set<String>}.
     */
    public Set<String> toSet() {
        return readValue(new TypeReference<LinkedHashSet<String>>() {}, new LinkedHashSet<>());
    }

    /**
     * Deserializes the JSON into a {@code List<String>}.
     */
    public List<String> toList() {
        return readValue(new TypeReference<ArrayList<String>>() {}, new ArrayList<>());
    }

    /**
     * Deserializes the JSON into a {@code Map<String, Object>}.
     */
    public Map<String, Object> toMap() {
        return readValue(new TypeReference<LinkedHashMap<String, Object>>() {}, new LinkedHashMap<>());
    }

    /**
     * Deserializes the JSON into any type via a Jackson {@link TypeReference}.
     * Returns the provided default if the JSON is null or empty.
     */
    public <T> T readValue(TypeReference<T> typeRef, T defaultValue) {
        if (json == null || json.isBlank()) {
            return defaultValue;
        }
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON column", e);
        }
    }

    /**
     * Deserializes the JSON into any type via a Jackson {@link TypeReference}.
     * Returns {@code null} if the JSON is null or empty.
     */
    public <T> T readValue(TypeReference<T> typeRef) {
        return readValue(typeRef, null);
    }

    // ── Accessors ───────────────────────────────────────────────────

    /**
     * Returns the raw JSON string. Used by the writing converter
     * and for debugging.
     */
    public String asString() {
        return json;
    }

    /**
     * Returns {@code true} if the underlying JSON is null or blank.
     */
    public boolean isNull() {
        return json == null || json.isBlank();
    }

    @Override
    public String toString() {
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonColumn that)) return false;
        return Objects.equals(json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(json);
    }
}
