package com.promptly.shared.config.r2dbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration that activates the PostgreSQL R2DBC persistence layer.
 * <p>
 * This configuration is enabled when {@code promptly.persistence.type} is {@code "postgres"}.
 * It imports the R2DBC config (connection factory, auditing, repository scanning)
 * and ensures all R2DBC adapter beans are discovered via component scanning.
 *
 * <h3>Dependencies (Maven profile: persistence-postgres):</h3>
 * <ul>
 *   <li>{@code spring-boot-starter-data-r2dbc}</li>
 *   <li>{@code r2dbc-postgresql}</li>
 *   <li>{@code flyway-database-postgresql}</li>
 *   <li>{@code spring-modulith-starter-jdbc} (event publication store)</li>
 *   <li>{@code pgvector} (vector search)</li>
 * </ul>
 *
 * @see com.promptly.shared.config.PersistenceProperties
 * @see R2dbcConfig
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "postgres")
@Import(R2dbcConfig.class)
public class R2dbcPersistenceAutoConfiguration {
    // All R2DBC adapters are discovered by @ConditionalOnProperty + @Component.
    // R2dbcConfig enables @EnableR2dbcRepositories for repository interface scanning.
}
