package com.spectrayan.promptly.auth.infrastructure.security;

import com.spectrayan.promptly.auth.application.port.in.ManageApiKeysUseCase;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationFilterTest {

    @Mock
    private ManageApiKeysUseCase manageApiKeysUseCase;

    private ApiKeyAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyAuthenticationFilter(manageApiKeysUseCase);
    }

    @Test
    @DisplayName("should authenticate successfully via X-API-Key header")
    void shouldAuthenticateViaXApiKeyHeader() {
        String rawKey = "prk_live_testkey1234567890123456789012";
        ApiKey key = ApiKey.builder()
                .id("key-id-123")
                .projectId("proj-abc")
                .name("Test Key")
                .build();

        when(manageApiKeysUseCase.authenticateApiKey(rawKey)).thenReturn(Mono.just(key));

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/deliver?appId=proj-abc")
                .header("X-API-Key", rawKey)
                .build();

        StepVerifier.create(filter.authenticate(request))
                .assertNext(auth -> {
                    assertThat(auth.getPrincipal()).isEqualTo("key-id-123");
                    assertThat(auth.getCredentials()).isEqualTo("proj-abc");
                    assertThat(auth.getAuthorities())
                            .extracting("authority")
                            .contains("ROLE_API_KEY", "ROLE_USER");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("should authenticate successfully via Authorization: Bearer prk_ header")
    void shouldAuthenticateViaBearerHeader() {
        String rawKey = "prk_live_testkey1234567890123456789012";
        ApiKey key = ApiKey.builder()
                .id("key-id-456")
                .projectId("proj-xyz")
                .build();

        when(manageApiKeysUseCase.authenticateApiKey(rawKey)).thenReturn(Mono.just(key));

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/deliver?appId=proj-xyz")
                .header("Authorization", "Bearer " + rawKey)
                .build();

        StepVerifier.create(filter.authenticate(request))
                .assertNext(auth -> {
                    assertThat(auth.getPrincipal()).isEqualTo("key-id-456");
                    assertThat(auth.getCredentials()).isEqualTo("proj-xyz");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("should return empty Mono if header is missing or not a prk key")
    void shouldReturnEmptyWhenNoKeyPresent() {
        MockServerHttpRequest request1 = MockServerHttpRequest.get("/api/v1/deliver?appId=proj-xyz").build();
        StepVerifier.create(filter.authenticate(request1)).verifyComplete();

        MockServerHttpRequest request2 = MockServerHttpRequest.get("/api/v1/deliver?appId=proj-xyz")
                .header("Authorization", "Bearer eyJhbGciOi...")
                .build();
        StepVerifier.create(filter.authenticate(request2)).verifyComplete();
    }
}
