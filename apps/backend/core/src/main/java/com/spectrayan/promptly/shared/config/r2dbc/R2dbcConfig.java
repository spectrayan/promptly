package com.spectrayan.promptly.shared.config.r2dbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spectrayan.promptly.shared.config.r2dbc.converter.JsonConverters;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * R2DBC configuration for SQL persistence (PostgreSQL, H2, SQLite).
 * <p>
 * Registers typed JSON converters that use the Spring-managed {@link ObjectMapper}
 * to transparently convert between database JSON/JSONB columns and Java types.
 * All JSON serialization is handled in the converter layer — adapters are
 * clean field-to-field mappers with no {@code ObjectMapper} dependency.
 * <p>
 * Domain-specific converters (e.g., FindingList) are contributed by their
 * respective modules via {@code List<Converter>} injection, keeping module
 * boundaries clean.
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@EnableR2dbcRepositories(basePackages = "com.spectrayan.promptly.**.infrastructure.persistence.r2dbc")
@EnableR2dbcAuditing
public class R2dbcConfig {

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory,
                                                         ObjectMapper objectMapper,
                                                         List<Converter<?, ?>> domainConverters) {
        var dialect = DialectResolver.getDialect(connectionFactory);
        var allConverters = new ArrayList<Object>();
        // Generic JSON converters for Set<String>, List<String>, Map<String,Object>
        allConverters.add(new JsonConverters.StringToSetConverter(objectMapper));
        allConverters.add(new JsonConverters.SetToStringConverter(objectMapper));
        allConverters.add(new JsonConverters.StringToListConverter(objectMapper));
        allConverters.add(new JsonConverters.ListToStringConverter(objectMapper));
        allConverters.add(new JsonConverters.StringToMapConverter(objectMapper));
        allConverters.add(new JsonConverters.MapToStringConverter(objectMapper));
        // Domain-specific converters contributed by other modules
        allConverters.addAll(domainConverters);
        return R2dbcCustomConversions.of(dialect, allConverters);
    }
}

