package com.promptly.shared.config.r2dbc.converter;

import java.util.Objects;

/**
 * Lightweight wrapper for JSON column values in R2DBC entities.
 * <p>
 * Using this type instead of raw {@code String} allows Spring Data R2DBC custom
 * converters ({@link JsonColumnReadingConverter} / {@link JsonColumnWritingConverter})
 * to target JSON columns precisely — without accidentally converting every
 * {@code String} field.
 * <p>
 * This is a pure value type — it holds the raw JSON string and nothing else.
 * All serialization / deserialization uses the Spring-managed {@code ObjectMapper}
 * bean in the adapter layer.
 *
 * <h3>Usage in entities:</h3>
 * <pre>{@code
 * @Column("tags")
 * private JsonColumn tags;
 * }</pre>
 *
 * <h3>Usage in adapters (with injected ObjectMapper):</h3>
 * <pre>{@code
 * // Writing (domain → entity)
 * .tags(JsonColumn.of(objectMapper.writeValueAsString(prompt.getTags())))
 *
 * // Reading (entity → domain)
 * objectMapper.readValue(entity.getTags().asString(), new TypeReference<Set<String>>() {})
 * }</pre>
 *
 * @see JsonColumnReadingConverter
 * @see JsonColumnWritingConverter
 */
public final class JsonColumn {

    private final String json;

    private JsonColumn(String json) {
        this.json = json;
    }

    /**
     * Creates a {@code JsonColumn} from a raw JSON string.
     */
    public static JsonColumn of(String json) {
        return json != null ? new JsonColumn(json) : null;
    }

    /**
     * Returns the raw JSON string.
     */
    public String asString() {
        return json;
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
