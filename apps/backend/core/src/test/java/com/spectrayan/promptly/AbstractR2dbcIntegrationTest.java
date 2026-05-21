package com.spectrayan.promptly;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared Testcontainers infrastructure for PostgreSQL R2DBC integration tests.
 * Starts a PostgreSQL container with pgvector support.
 * <p>
 * Uses the <strong>singleton container pattern</strong>: the container is started once
 * in a static initializer and reused across all test classes in the same JVM.
 * This avoids port-mismatch issues when Spring caches the application context
 * across multiple {@code @SpringBootTest} classes.
 * <p>
 * All R2DBC integration tests should extend this class.
 * Activates the {@code postgres} Spring profile which:
 * <ul>
 *   <li>Sets {@code promptly.persistence.type=postgres}</li>
 *   <li>Excludes MongoDB auto-configuration</li>
 *   <li>Enables Flyway migrations against the Testcontainer</li>
 * </ul>
 */
@ActiveProfiles("postgres")
public abstract class AbstractR2dbcIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>(
                DockerImageName.parse("pgvector/pgvector:pg17")
        )
                .withDatabaseName("promptly_test")
                .withUsername("promptly")
                .withPassword("promptly");
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // R2DBC connection
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        POSTGRES.getHost(),
                        POSTGRES.getMappedPort(5432),
                        POSTGRES.getDatabaseName()));
        registry.add("spring.r2dbc.username", POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", POSTGRES::getPassword);

        // Flyway (JDBC) — Flyway doesn't support R2DBC, needs JDBC URL
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);

        // JDBC DataSource for Spring Modulith event publication
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Persistence type
        registry.add("promptly.persistence.type", () -> "postgres");

        // Disable Vertex AI for tests
        registry.add("spring.ai.vertex.ai.gemini.project-id", () -> "test-project");
        registry.add("spring.ai.vertex.ai.gemini.location", () -> "us-central1");
    }
}
