package com.promptly.shared.config.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

/**
 * Auto-configuration that activates the MongoDB persistence layer.
 * <p>
 * This configuration is enabled when:
 * <ul>
 *   <li>{@code promptly.persistence.type} is {@code "mongo"} (or unset — default)</li>
 *   <li>{@code ReactiveMongoTemplate} is on the classpath (i.e., the MongoDB driver is present)</li>
 * </ul>
 *
 * <p>It is intentionally a marker configuration. All MongoDB adapters, repositories,
 * and the {@link MongoConfig} are already annotated with their own
 * {@code @ConditionalOnProperty} guards and scanned via standard Spring component
 * scanning. This class serves as the documentation entry point for the Mongo
 * persistence stack and a hook for any future cross-cutting Mongo beans.
 *
 * @see com.promptly.shared.config.PersistenceProperties
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@ConditionalOnClass(ReactiveMongoTemplate.class)
public class MongoPersistenceAutoConfiguration {
    // All Mongo beans are self-guarded via @ConditionalOnProperty.
    // This class groups them logically and provides classpath safety.
}
