package com.spectrayan.promptly.shared.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicit Jackson {@link ObjectMapper} bean for the WebFlux stack.
 * <p>
 * Spring Boot's {@code JacksonAutoConfiguration} normally provides this,
 * but a transitive dependency can interfere with auto-configuration
 * ordering. Defining the bean explicitly guarantees it is available
 * for all components — in particular
 * {@link com.spectrayan.promptly.shared.exception.GlobalErrorWebExceptionHandler}.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
