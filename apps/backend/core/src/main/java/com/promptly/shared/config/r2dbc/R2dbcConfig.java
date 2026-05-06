package com.promptly.shared.config.r2dbc;

import com.promptly.shared.config.r2dbc.converter.JsonColumnReadingConverter;
import com.promptly.shared.config.r2dbc.converter.JsonColumnWritingConverter;
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
 * Enables R2DBC repositories, auditing (createdAt/updatedAt auto-population),
 * and custom JSON column converters.
 * <p>
 * The {@link JsonColumnReadingConverter} and {@link JsonColumnWritingConverter}
 * handle transparent JSON ↔ Java serialization for entity fields typed as
 * {@link com.promptly.shared.config.r2dbc.converter.JsonColumn}. This keeps
 * adapters free of manual {@code ObjectMapper} calls for JSON column handling.
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

    /**
     * Registers custom R2DBC converters for JSON column handling.
     * <p>
     * These converters enable transparent serialization between the
     * database's native JSON/JSONB type and the application's
     * {@link com.promptly.shared.config.r2dbc.converter.JsonColumn} wrapper.
     */
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory) {
        var dialect = DialectResolver.getDialect(connectionFactory);
        return R2dbcCustomConversions.of(dialect, List.of(
                new JsonColumnReadingConverter(),
                new JsonColumnWritingConverter()
        ));
    }
}
