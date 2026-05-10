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

    @Container
    static final MongoDBContainer MONGODB = new MongoDBContainer(
            DockerImageName.parse("mongo:8.0")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGODB::getReplicaSetUrl);
        registry.add("spring.data.mongodb.host", MONGODB::getHost);
        registry.add("spring.data.mongodb.port", () -> MONGODB.getMappedPort(27017));
        
        // Disable Vertex AI for unit/integration tests
        registry.add("spring.ai.vertex.ai.gemini.project-id", () -> "test-project");
        registry.add("spring.ai.vertex.ai.gemini.location", () -> "us-central1");
    }

}
