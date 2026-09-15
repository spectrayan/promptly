package com.spectrayan.promptly;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared Testcontainers infrastructure for integration tests.
 * Starts a MongoDB Atlas Local container that supports vector search.
 * All integration tests should extend this class to share the container.
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

    static final MongoDBContainer MONGODB;

    static {
        MONGODB = new MongoDBContainer(
                DockerImageName.parse("mongo:8.0")
        );
        MONGODB.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGODB::getReplicaSetUrl);
        registry.add("spring.mongodb.uri", MONGODB::getReplicaSetUrl);
        registry.add("spring.data.mongodb.host", MONGODB::getHost);
        registry.add("spring.data.mongodb.port", () -> MONGODB.getMappedPort(27017));
        
        // Dummy API key for Google GenAI in tests
        registry.add("spring.ai.google.genai.api-key", () -> "test-api-key");
    }

}
