package com.spectrayan.promptly.scanner.infrastructure.persistence.r2dbc.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * R2DBC writing converter: {@link FindingList} → database JSON {@code String}.
 */
@WritingConverter
public class FindingListWritingConverter implements Converter<FindingList, String> {

    private final ObjectMapper objectMapper;

    public FindingListWritingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convert(FindingList source) {
        try {
            return objectMapper.writeValueAsString(source.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error writing findings JSON", e);
        }
    }
}
