package com.promptly.shared.exception;

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
 * Global exception handler using RFC 7807 Problem Details.
 * Returns structured error responses for all modules.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ProblemDetail> handleNotFound(ResourceNotFoundException ex, ServerWebExchange exchange) {
        log.warn("Resource not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://promptly.dev/errors/not-found"));
        problem.setProperty("timestamp", Instant.now());
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return Mono.just(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ProblemDetail> handleBadRequest(IllegalArgumentException ex, ServerWebExchange exchange) {
        log.warn("Bad request: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Bad Request");
        problem.setType(URI.create("https://promptly.dev/errors/bad-request"));
        problem.setProperty("timestamp", Instant.now());
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return Mono.just(problem);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ProblemDetail> handleGenericException(Exception ex, ServerWebExchange exchange) {
        String rootTrace = ExceptionUtils.getRootCauseMessage(ex);
        log.error("Unexpected error on {}: {} | Root cause: {}", exchange.getRequest().getPath(), ex.getMessage(), rootTrace);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://promptly.dev/errors/internal"));
        problem.setProperty("timestamp", Instant.now());
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return Mono.just(problem);
    }

}
