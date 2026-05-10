package com.spectrayan.promptly.shared.exception;

import com.spectrayan.promptly.infrastructure.in.web.dto.ProblemDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 * Validates RFC 9457 Problem Details mapping for each exception type.
 */
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    private MockServerWebExchange exchange(String path) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get(path).build());
    }

    // ── Domain exceptions ──────────────────────────────────────────

    @Test
    @DisplayName("ResourceNotFoundException → 404 with 'not-found' type")
    void shouldHandleNotFound() {
        var ex = new ResourceNotFoundException("Prompt", "p-123");

        StepVerifier.create(handler.handleNotFound(ex, exchange("/api/v1/prompts/p-123")))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    ProblemDetails body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.getStatus()).isEqualTo(404);
                    assertThat(body.getTitle()).isEqualTo("Resource Not Found");
                    assertThat(body.getCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
                    assertThat(body.getType().toString()).contains("not-found");
                    assertThat(body.getInstance().toString()).isEqualTo("/api/v1/prompts/p-123");
                    assertThat(body.getTimestamp()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("DuplicateResourceException → 409 with 'conflict' type")
    void shouldHandleDuplicate() {
        var ex = new DuplicateResourceException(ErrorCode.PROMPT_DUPLICATE_NAME, "Prompt 'X' already exists");

        StepVerifier.create(handler.handleDuplicate(ex, exchange("/api/v1/prompts")))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    ProblemDetails body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.getStatus()).isEqualTo(409);
                    assertThat(body.getTitle()).isEqualTo("Duplicate Resource");
                    assertThat(body.getCode()).isEqualTo(ErrorCode.PROMPT_DUPLICATE_NAME);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("IllegalStateException → 409 BUSINESS_RULE_VIOLATION")
    void shouldHandleBusinessRule() {
        var ex = new IllegalStateException("Prompt is not editable");

        StepVerifier.create(handler.handleIllegalState(ex, exchange("/api/v1/prompts/p-1")))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.BUSINESS_RULE_VIOLATION);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("IllegalArgumentException → 400 BAD_REQUEST")
    void shouldHandleBadRequest() {
        var ex = new IllegalArgumentException("Invalid page size");

        StepVerifier.create(handler.handleBadRequest(ex, exchange("/api/v1/prompts")))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
                    assertThat(response.getBody().getDetail()).isEqualTo("Invalid page size");
                })
                .verifyComplete();
    }

    // ── Catch-all ──────────────────────────────────────────────────

    @Test
    @DisplayName("Generic Exception → 500 INTERNAL_ERROR (detail masked)")
    void shouldHandleGenericException() {
        var ex = new RuntimeException("Something broke internally");

        StepVerifier.create(handler.handleGenericException(ex, exchange("/api/v1/prompts")))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    ProblemDetails body = response.getBody();
                    assertThat(body.getCode()).isEqualTo(ErrorCode.INTERNAL_ERROR);
                    // Detail should NOT leak internal message
                    assertThat(body.getDetail()).doesNotContain("Something broke");
                })
                .verifyComplete();
    }

    // ── Framework exceptions ───────────────────────────────────────

    @Test
    @DisplayName("ServerWebInputException → 400")
    void shouldHandleWebInput() {
        var ex = new org.springframework.web.server.ServerWebInputException("Bad JSON body");

        StepVerifier.create(handler.handleWebInput(ex, exchange("/api/v1/prompts")))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("MethodNotAllowedException → 405")
    void shouldHandleMethodNotAllowed() {
        var ex = new org.springframework.web.server.MethodNotAllowedException("DELETE", null);

        StepVerifier.create(handler.handleMethodNotAllowed(ex, exchange("/api/v1/prompts")))
                .assertNext(response ->
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED))
                .verifyComplete();
    }
}
