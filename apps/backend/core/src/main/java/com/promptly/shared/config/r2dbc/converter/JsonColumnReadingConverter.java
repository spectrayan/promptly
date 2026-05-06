package com.promptly.shared.config.r2dbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * R2DBC reading converter: database {@code String} → {@link JsonColumn}.
 * <p>
 * When Spring Data R2DBC reads a column from the database and the target
 * entity field is typed as {@code JsonColumn}, this converter is invoked
 * automatically. Works with both:
 * <ul>
 *   <li>PostgreSQL JSONB (driver returns {@code String} via codec)</li>
 *   <li>H2 JSON (driver returns {@code String})</li>
 * </ul>
 *
 * @see JsonColumn
 * @see JsonColumnWritingConverter
 */
@ReadingConverter
public class JsonColumnReadingConverter implements Converter<String, JsonColumn> {

    @Override
    public JsonColumn convert(String source) {
        return JsonColumn.of(source);
    }
}
