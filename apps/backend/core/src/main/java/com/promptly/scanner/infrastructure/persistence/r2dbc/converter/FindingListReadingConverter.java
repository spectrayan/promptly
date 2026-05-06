package com.promptly.scanner.infrastructure.persistence.r2dbc.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptly.scanner.domain.model.Finding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.io.IOException;
import java.util.List;

/**
 * R2DBC reading converter: database JSON {@code String} → {@link FindingList}.
 */
@ReadingConverter
public class FindingListReadingConverter implements Converter<String, FindingList> {

    private final ObjectMapper objectMapper;

    public FindingListReadingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public FindingList convert(String source) {
        try {
            List<Finding> findings = objectMapper.readValue(source, new TypeReference<>() {});
            return new FindingList(findings);
        } catch (IOException e) {
            throw new RuntimeException("Error reading findings JSON", e);
        }
    }
}
