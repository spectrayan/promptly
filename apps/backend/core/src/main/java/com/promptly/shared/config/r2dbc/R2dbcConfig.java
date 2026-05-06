package com.promptly.shared.config.r2dbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptly.scanner.infrastructure.persistence.r2dbc.converter.FindingListReadingConverter;
import com.promptly.scanner.infrastructure.persistence.r2dbc.converter.FindingListWritingConverter;
import com.promptly.shared.config.r2dbc.converter.JsonConverters;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

import java.util.List;

/**
 * R2DBC configuration for SQL persistence (PostgreSQL, H2, SQLite).
 * <p>
 * Registers typed JSON converters that use the Spring-managed {@link ObjectMapper}
 * to transparently convert between database JSON/JSONB columns and Java types.
 * All JSON serialization is handled in the converter layer — adapters are
 * clean field-to-field mappers with no {@code ObjectMapper} dependency.
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@EnableR2dbcRepositories(basePackages = "com.promptly.**.infrastructure.persistence.r2dbc")
@EnableR2dbcAuditing
public class R2dbcConfig {

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory,
                                                         ObjectMapper objectMapper) {
        var dialect = DialectResolver.getDialect(connectionFactory);
        return R2dbcCustomConversions.of(dialect, List.of(
                // Generic JSON converters for Set<String>, List<String>, Map<String,Object>
                new JsonConverters.StringToSetConverter(objectMapper),
                new JsonConverters.SetToStringConverter(objectMapper),
                new JsonConverters.StringToListConverter(objectMapper),
                new JsonConverters.ListToStringConverter(objectMapper),
                new JsonConverters.StringToMapConverter(objectMapper),
                new JsonConverters.MapToStringConverter(objectMapper),
                // Domain-specific: FindingList (List<Finding>)
                new FindingListReadingConverter(objectMapper),
                new FindingListWritingConverter(objectMapper)
        ));
    }
}
