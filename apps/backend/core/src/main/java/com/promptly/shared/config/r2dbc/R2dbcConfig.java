package com.promptly.shared.config.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

/**
 * R2DBC configuration for SQL persistence (PostgreSQL, H2, SQLite).
 * <p>
 * Enables R2DBC repositories and auditing (createdAt, updatedAt auto-population)
 * only when the persistence type is set to {@code "sql"}.
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
}
