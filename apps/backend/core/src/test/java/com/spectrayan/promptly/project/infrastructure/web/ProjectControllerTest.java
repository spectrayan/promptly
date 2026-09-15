package com.spectrayan.promptly.project.infrastructure.web;

import com.spectrayan.promptly.auth.application.port.in.ManageApiKeysUseCase;
import com.spectrayan.promptly.auth.application.port.in.UserQueryUseCase;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
import com.spectrayan.promptly.infrastructure.in.web.dto.CreateApiKeyRequest;
import com.spectrayan.promptly.project.application.port.in.CreateProjectUseCase;
import com.spectrayan.promptly.project.application.port.in.GetProjectUseCase;
import com.spectrayan.promptly.project.application.port.in.ManageProjectMembersUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock private CreateProjectUseCase createProjectUseCase;
    @Mock private GetProjectUseCase getProjectUseCase;
    @Mock private ManageProjectMembersUseCase manageMembersUseCase;
    @Mock private ManageApiKeysUseCase manageApiKeysUseCase;
    @Mock private UserQueryUseCase userQueryUseCase;

    private ProjectController controller;

    @BeforeEach
    void setUp() {
        controller = new ProjectController(
                createProjectUseCase,
                getProjectUseCase,
                manageMembersUseCase,
                manageApiKeysUseCase,
                userQueryUseCase
        );
    }

    @Test
    @DisplayName("createProjectApiKey should return 201 Created with plaintext key and metadata")
    void createProjectApiKey_shouldReturnCreated() {
        ApiKey apiKey = ApiKey.builder()
                .id("key-1")
                .projectId("proj-1")
                .name("CI Key")
                .keyPrefix("prk_live_abcd...")
                .keyHash("hash123")
                .roles(List.of("ROLE_API_KEY"))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        var generated = new ManageApiKeysUseCase.GeneratedApiKey(apiKey, "prk_live_abcd123456789012345678901234");

        when(manageApiKeysUseCase.createApiKey(eq("proj-1"), eq("CI Key"), eq(30), any()))
                .thenReturn(Mono.just(generated));

        CreateApiKeyRequest req = new CreateApiKeyRequest();
        req.setName("CI Key");
        req.setExpiresInDays(30);

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/v1/projects/proj-1/api-keys").build());

        StepVerifier.create(controller.createProjectApiKey("proj-1", Mono.just(req), exchange))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getId()).isEqualTo("key-1");
                    assertThat(response.getBody().getApiKey()).isEqualTo("prk_live_abcd123456789012345678901234");
                    assertThat(response.getBody().getPrefix()).isEqualTo("prk_live_abcd...");
                    assertThat(response.getBody().getName()).isEqualTo("CI Key");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("listProjectApiKeys should return Flux of ApiKeyResponse")
    void listProjectApiKeys_shouldReturnKeys() {
        ApiKey apiKey1 = ApiKey.builder()
                .id("key-1")
                .projectId("proj-1")
                .name("Key 1")
                .keyPrefix("prk_live_1111...")
                .createdAt(Instant.now())
                .build();
        ApiKey apiKey2 = ApiKey.builder()
                .id("key-2")
                .projectId("proj-1")
                .name("Key 2")
                .keyPrefix("prk_live_2222...")
                .createdAt(Instant.now())
                .build();

        when(manageApiKeysUseCase.listApiKeys("proj-1")).thenReturn(Flux.just(apiKey1, apiKey2));

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/projects/proj-1/api-keys").build());

        StepVerifier.create(controller.listProjectApiKeys("proj-1", exchange))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    StepVerifier.create(response.getBody())
                            .expectNextMatches(k -> k.getId().equals("key-1") && k.getName().equals("Key 1"))
                            .expectNextMatches(k -> k.getId().equals("key-2") && k.getName().equals("Key 2"))
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("revokeProjectApiKey should return 204 No Content")
    void revokeProjectApiKey_shouldReturnNoContent() {
        when(manageApiKeysUseCase.revokeApiKey("proj-1", "key-1")).thenReturn(Mono.empty());

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.delete("/api/v1/projects/proj-1/api-keys/key-1").build());

        StepVerifier.create(controller.revokeProjectApiKey("proj-1", "key-1", exchange))
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT))
                .verifyComplete();
    }
}
