package com.promptly.shared.exception;

import com.promptly.infrastructure.in.web.dto.ProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import com.promptly.shared.util.ExceptionUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Global exception handler using RFC 9457 Problem Details.
 * <p>
 * Returns our contract-first {@link ProblemDetails} DTO (generated from the OpenAPI
 * spec) so the response shape is identical to what SDK clients expect.
 * <p>
 * Every response includes a {@code code} extension property — a machine-readable
 * error code the frontend uses to look up its own localised, user-friendly message.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Domain exceptions ──────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleNotFound(
            ResourceNotFoundException ex, ServerWebExchange exchange) {
        log.warn("Resource not found: {}", ex.getMessage());
        return Mono.just(respond(HttpStatus.NOT_FOUND, "Resource Not Found",
                ex.getMessage(), "not-found", ex.getCode(), exchange));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleDuplicate(
            DuplicateResourceException ex, ServerWebExchange exchange) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return Mono.just(respond(HttpStatus.CONFLICT, "Duplicate Resource",
                ex.getMessage(), "conflict", ex.getCode(), exchange));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleIllegalState(
            IllegalStateException ex, ServerWebExchange exchange) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return Mono.just(respond(HttpStatus.CONFLICT, "Business Rule Violation",
                ex.getMessage(), "conflict", ErrorCode.BUSINESS_RULE_VIOLATION, exchange));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleAuthenticationFailed(
            AuthenticationFailedException ex, ServerWebExchange exchange) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return Mono.just(respond(HttpStatus.UNAUTHORIZED, "Unauthorized",
                ex.getMessage(), "unauthorized", ex.getCode(), exchange));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleBadRequest(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        log.warn("Bad request: {}", ex.getMessage());
        return Mono.just(respond(HttpStatus.BAD_REQUEST, "Bad Request",
                ex.getMessage(), "bad-request", ErrorCode.BAD_REQUEST, exchange));
    }

    // ── Database duplicate key (works for both MongoDB and R2DBC/JDBC) ─

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleDataIntegrity(
            DataIntegrityViolationException ex, ServerWebExchange exchange) {
        String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        if (msg.contains("duplicate key") || msg.contains("unique constraint") || msg.contains("11000")) {
            log.warn("Duplicate key on {}: {}", exchange.getRequest().getPath(), ex.getMessage());
            return Mono.just(respond(HttpStatus.CONFLICT, "Duplicate Resource",
                    ErrorMessages.DUPLICATE_RESOURCE_GENERIC, "conflict",
                    ErrorCode.DUPLICATE_RESOURCE, exchange));
        }
        return handleGenericException(ex, exchange);
    }

    // ── Spring WebFlux framework exceptions ────────────────────────────

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleWebInput(
            ServerWebInputException ex, ServerWebExchange exchange) {
        log.warn("Malformed request: {}", ex.getReason());
        return Mono.just(respond(HttpStatus.BAD_REQUEST, "Bad Request",
                ex.getReason(), "bad-request", ErrorCode.BAD_REQUEST, exchange));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleValidation(
            WebExchangeBindException ex, ServerWebExchange exchange) {
        log.warn("Validation failed: {} errors", ex.getFieldErrorCount());
        ProblemDetails problem = buildProblem(HttpStatus.BAD_REQUEST,
                "Validation Error", "One or more fields failed validation",
                "validation-error", ErrorCode.BAD_REQUEST, exchange);

        var validationErrors = ex.getFieldErrors().stream()
                .map(fe -> {
                    var ve = new com.promptly.infrastructure.in.web.dto.ValidationError();
                    ve.setField(fe.getField());
                    ve.setMessage(fe.getDefaultMessage());
                    ve.setRejectedValue(fe.getRejectedValue() != null ? fe.getRejectedValue().toString() : null);
                    return ve;
                })
                .toList();
        problem.setErrors(validationErrors);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem));
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleMethodNotAllowed(
            MethodNotAllowedException ex, ServerWebExchange exchange) {
        log.warn("Method not allowed: {}", ex.getReason());
        return Mono.just(respond(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                ex.getReason(), "method-not-allowed", ErrorCode.BAD_REQUEST, exchange));
    }

    @ExceptionHandler(NotAcceptableStatusException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleNotAcceptable(
            NotAcceptableStatusException ex, ServerWebExchange exchange) {
        log.warn("Not acceptable: {}", ex.getReason());
        return Mono.just(respond(HttpStatus.NOT_ACCEPTABLE, "Not Acceptable",
                ex.getReason(), "not-acceptable", ErrorCode.BAD_REQUEST, exchange));
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleUnsupportedMedia(
            UnsupportedMediaTypeStatusException ex, ServerWebExchange exchange) {
        log.warn("Unsupported media type: {}", ex.getReason());
        return Mono.just(respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type",
                ex.getReason(), "unsupported-media-type", ErrorCode.BAD_REQUEST, exchange));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ProblemDetails>> handleResponseStatus(
            ResponseStatusException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.warn("ResponseStatusException [{}]: {}", status, ex.getReason());
        return Mono.just(respond(status, status.getReasonPhrase(),
                ex.getReason(), "error", ErrorCode.INTERNAL_ERROR, exchange));
    }

    // ── Catch-all ──────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetails>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        String rootTrace = ExceptionUtils.getRootCauseMessage(ex);
        log.error("Unexpected error on {}: {} | Root cause: {}",
                exchange.getRequest().getPath(), ex.getMessage(), rootTrace);
        return Mono.just(respond(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                ErrorMessages.UNEXPECTED_ERROR, "internal", ErrorCode.INTERNAL_ERROR, exchange));
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    private ResponseEntity<ProblemDetails> respond(HttpStatus status, String title,
                                                    String detail, String typeSlug,
                                                    String code, ServerWebExchange exchange) {
        return ResponseEntity.status(status).body(
                buildProblem(status, title, detail, typeSlug, code, exchange));
    }

    private ProblemDetails buildProblem(HttpStatus status, String title,
                                         String detail, String typeSlug,
                                         String code, ServerWebExchange exchange) {
        ProblemDetails p = new ProblemDetails();
        p.setType(URI.create("https://promptly.dev/errors/" + typeSlug));
        p.setTitle(title);
        p.setStatus(status.value());
        p.setDetail(detail);
        p.setCode(code);
        p.setInstance(URI.create(exchange.getRequest().getPath().value()));
        p.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return p;
    }
}
