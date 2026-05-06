package com.promptly.shared.config.r2dbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * R2DBC writing converter: {@link JsonColumn} → database {@code String}.
 * <p>
 * When Spring Data R2DBC persists an entity that has {@code JsonColumn} fields,
 * this converter serializes them to a JSON string that is written to the column.
 * Works with both:
 * <ul>
 *   <li>PostgreSQL JSONB columns (driver accepts String for JSONB inserts)</li>
 *   <li>H2 JSON columns (driver accepts String for JSON inserts)</li>
 * </ul>
 *
 * @see JsonColumn
 * @see JsonColumnReadingConverter
 */
@WritingConverter
public class JsonColumnWritingConverter implements Converter<JsonColumn, String> {

    @Override
    public String convert(JsonColumn source) {
        return source.asString();
    }
}
