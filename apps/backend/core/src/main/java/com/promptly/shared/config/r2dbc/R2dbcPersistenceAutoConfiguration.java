package com.promptly.shared.config.r2dbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration that activates the SQL R2DBC persistence layer.
 * <p>
 * This configuration is enabled when {@code promptly.persistence.type} is {@code "sql"}.
 * It imports the R2DBC config (connection factory, auditing, repository scanning)
 * and ensures all R2DBC adapter beans are discovered via component scanning.
 * <p>
 * Supports PostgreSQL, H2, and SQLite via their respective R2DBC drivers.
 * The specific SQL dialect is determined by the connection URL at runtime.
 *
 * <h3>Dependencies (Maven profile: persistence-sql):</h3>
 * <ul>
 *   <li>{@code spring-boot-starter-data-r2dbc}</li>
 *   <li>One of: {@code r2dbc-postgresql}, {@code r2dbc-h2}</li>
 *   <li>{@code spring-boot-starter-flyway} (schema migration)</li>
 *   <li>{@code spring-modulith-starter-jdbc} (event publication store)</li>
 * </ul>
 *
 * @see com.promptly.shared.config.PersistenceProperties
 * @see R2dbcConfig
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@Import(R2dbcConfig.class)
public class R2dbcPersistenceAutoConfiguration {
    // All R2DBC adapters are discovered by @ConditionalOnProperty + @Component.
    // R2dbcConfig enables @EnableR2dbcRepositories for repository interface scanning.
}
