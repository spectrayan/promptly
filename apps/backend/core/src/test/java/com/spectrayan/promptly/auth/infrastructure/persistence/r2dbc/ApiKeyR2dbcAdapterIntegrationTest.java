package com.spectrayan.promptly.auth.infrastructure.persistence.r2dbc;

import com.spectrayan.promptly.AbstractR2dbcIntegrationTest;
import com.spectrayan.promptly.auth.application.port.out.ApiKeyPersistencePort;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
import com.spectrayan.promptly.project.application.port.out.ProjectPersistencePort;
import com.spectrayan.promptly.project.domain.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApiKeyR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private ApiKeyPersistencePort apiKeyPort;

    @Autowired
    private ProjectPersistencePort projectPort;

    private String projectId;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID().toString();
        Project project = Project.builder()
                .id(projectId)
                .name("R2DBC ApiKey Project " + UUID.randomUUID())
                .createdBy("usr-1")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        projectPort.save(project).block();
    }

    private ApiKey buildApiKey(String name, String hash) {
        return ApiKey.builder()
                .id(UUID.randomUUID().toString())
                .projectId(projectId)
                .name(name)
                .keyPrefix("prk_live_test...")
                .keyHash(hash)
                .roles(List.of("ROLE_API_KEY", "ROLE_DELIVERY"))
                .revoked(false)
                .createdBy("usr-1")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("should save and find API key by ID")
    void shouldSaveAndFindApiKeyById() {
        String hash = "hash-" + UUID.randomUUID();
        ApiKey key = buildApiKey("Test Key 1", hash);

        StepVerifier.create(
                apiKeyPort.save(key)
                        .flatMap(saved -> apiKeyPort.findById(saved.getId()))
        )
        .assertNext(found -> {
            assertThat(found.getId()).isEqualTo(key.getId());
            assertThat(found.getName()).isEqualTo("Test Key 1");
            assertThat(found.getProjectId()).isEqualTo(projectId);
            assertThat(found.getKeyHash()).isEqualTo(hash);
            assertThat(found.getRoles()).contains("ROLE_API_KEY", "ROLE_DELIVERY");
            assertThat(found.isRevoked()).isFalse();
        })
        .verifyComplete();
    }

    @Test
    @DisplayName("should find API key by key hash")
    void shouldFindByKeyHash() {
        String hash = "hash-lookup-" + UUID.randomUUID();
        ApiKey key = buildApiKey("Lookup Key", hash);

        StepVerifier.create(
                apiKeyPort.save(key)
                        .then(apiKeyPort.findByKeyHash(hash))
        )
        .assertNext(found -> {
            assertThat(found.getId()).isEqualTo(key.getId());
            assertThat(found.getKeyHash()).isEqualTo(hash);
        })
        .verifyComplete();
    }

    @Test
    @DisplayName("should find all API keys by project ID")
    void shouldFindByProjectId() {
        ApiKey key1 = buildApiKey("Project Key 1", "hash-p1-" + UUID.randomUUID());
        ApiKey key2 = buildApiKey("Project Key 2", "hash-p2-" + UUID.randomUUID());

        StepVerifier.create(
                apiKeyPort.save(key1)
                        .then(apiKeyPort.save(key2))
                        .thenMany(apiKeyPort.findByProjectId(projectId))
                        .collectList()
        )
        .assertNext(list -> {
            assertThat(list).extracting("id").contains(key1.getId(), key2.getId());
        })
        .verifyComplete();
    }

    @Test
    @DisplayName("should check exists by ID and project ID")
    void shouldCheckExistsByIdAndProjectId() {
        ApiKey key = buildApiKey("Exists Key", "hash-exists-" + UUID.randomUUID());

        StepVerifier.create(
                apiKeyPort.save(key)
                        .then(apiKeyPort.existsByIdAndProjectId(key.getId(), projectId))
        )
        .expectNext(true)
        .verifyComplete();
    }
}
