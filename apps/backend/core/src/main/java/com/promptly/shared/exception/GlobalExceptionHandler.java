package com.promptly.shared.exception;

import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import com.promptly.shared.util.ExceptionUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

/**
 * Global exception handler using RFC 9457 Problem Details.
 * <p>
 * Every response includes a {@code code} extension property — a machine-readable
 * error code the frontend uses to look up its own localised, user-friendly message.
 * The {@code detail} field serves as a developer-facing fallback.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ProblemDetail> handleNotFound(ResourceNotFoundException ex, ServerWebExchange exchange) {
        log.warn("Resource not found: {}", ex.getMessage());
        return Mono.just(buildProblem(
                HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(),
                "https://promptly.dev/errors/not-found", ex.getCode(), exchange));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Mono<ProblemDetail> handleDuplicate(DuplicateResourceException ex, ServerWebExchange exchange) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return Mono.just(buildProblem(
                HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(),
                "https://promptly.dev/errors/conflict", ex.getCode(), exchange));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ProblemDetail> handleIllegalState(IllegalStateException ex, ServerWebExchange exchange) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return Mono.just(buildProblem(
                HttpStatus.CONFLICT, "Business Rule Violation", ex.getMessage(),
                "https://promptly.dev/errors/conflict", ErrorCode.BUSINESS_RULE_VIOLATION, exchange));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ProblemDetail> handleBadRequest(IllegalArgumentException ex, ServerWebExchange exchange) {
        log.warn("Bad request: {}", ex.getMessage());
        return Mono.just(buildProblem(
                HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(),
                "https://promptly.dev/errors/bad-request", ErrorCode.BAD_REQUEST, exchange));
    }

    @ExceptionHandler(MongoWriteException.class)
    public Mono<ProblemDetail> handleMongoWrite(MongoWriteException ex, ServerWebExchange exchange) {
        if (ex.getError().getCode() == 11000) {
            log.warn("MongoDB duplicate key on {}: {}", exchange.getRequest().getPath(), ex.getError().getMessage());
            return Mono.just(buildProblem(
                    HttpStatus.CONFLICT, "Duplicate Resource", ErrorMessages.DUPLICATE_RESOURCE_GENERIC,
                    "https://promptly.dev/errors/conflict", ErrorCode.DUPLICATE_RESOURCE, exchange));
        }
        return handleGenericException(ex, exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ProblemDetail> handleGenericException(Exception ex, ServerWebExchange exchange) {
        String rootTrace = ExceptionUtils.getRootCauseMessage(ex);
        log.error("Unexpected error on {}: {} | Root cause: {}", exchange.getRequest().getPath(), ex.getMessage(), rootTrace);
        return Mono.just(buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ErrorMessages.UNEXPECTED_ERROR,
                "https://promptly.dev/errors/internal", ErrorCode.INTERNAL_ERROR, exchange));
    }

    // ── Helper ─────────────────────────────────────────────────────────

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail,
                                       String typeUri, String code, ServerWebExchange exchange) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(typeUri));
        problem.setProperty("code", code);
        problem.setProperty("timestamp", Instant.now());
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return problem;
    }
}
