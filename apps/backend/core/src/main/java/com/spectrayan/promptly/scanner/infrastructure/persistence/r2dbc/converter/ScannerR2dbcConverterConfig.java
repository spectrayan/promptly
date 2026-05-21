package com.spectrayan.promptly.scanner.infrastructure.persistence.r2dbc.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * Registers scanner-specific R2DBC converters as Spring beans.
 * <p>
 * These converters are automatically picked up by
 * {@code R2dbcConfig.r2dbcCustomConversions} via the injected
 * {@code List<Converter>}, keeping the module boundary clean
 * (shared never imports scanner internals).
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
public class ScannerR2dbcConverterConfig {

    @Bean
    public Converter<String, FindingList> findingListReadingConverter(ObjectMapper objectMapper) {
        return new FindingListReadingConverter(objectMapper);
    }

    @Bean
    public Converter<FindingList, String> findingListWritingConverter(ObjectMapper objectMapper) {
        return new FindingListWritingConverter(objectMapper);
    }
}
