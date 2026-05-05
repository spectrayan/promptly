package com.promptly.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Promptly persistence layer.
 * <p>
 * Determines which database adapter set is activated at runtime.
 * Supported values for {@code type}:
 * <ul>
 *   <li>{@code mongo} — MongoDB (default)</li>
 *   <li>{@code sql} — SQL databases via R2DBC (PostgreSQL, H2, SQLite)</li>
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
     * Returns whether the active persistence type is SQL (PostgreSQL, H2, SQLite).
     */
    public boolean isSql() {
        return "sql".equalsIgnoreCase(type);
    }

    /**
     * @deprecated Use {@link #isSql()} instead. Retained for backward compatibility.
     */
    @Deprecated(forRemoval = true)
    public boolean isPostgres() {
        return isSql();
    }
}
