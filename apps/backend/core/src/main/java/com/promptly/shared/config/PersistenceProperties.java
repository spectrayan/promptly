package com.promptly.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Promptly persistence layer.
 * <p>
 * Determines which database adapter set is activated at runtime.
 * Supported values for {@code type}:
 * <ul>
 *   <li>{@code mongo} — MongoDB (default)</li>
 *   <li>{@code postgres} — PostgreSQL via R2DBC</li>
 * </ul>
 *
 * @param type the persistence backend type (default: "mongo")
 */
@ConfigurationProperties(prefix = "promptly.persistence")
public record PersistenceProperties(String type) {

    /**
     * Returns whether the active persistence type is MongoDB.
     */
    public boolean isMongo() {
        return "mongo".equalsIgnoreCase(type);
    }

    /**
     * Returns whether the active persistence type is PostgreSQL.
     */
    public boolean isPostgres() {
        return "postgres".equalsIgnoreCase(type);
    }
}
