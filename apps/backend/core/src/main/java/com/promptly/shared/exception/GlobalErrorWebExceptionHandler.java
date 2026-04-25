package com.promptly.shared.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptly.infrastructure.in.web.dto.ProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Safety-net exception handler for errors that occur <em>outside</em> the
 * {@code @RestControllerAdvice} scope — e.g. routing failures (404 for unknown
 * paths), security filter errors, or codec failures.
 * <p>
 * Registered with {@code @Order(-2)} to run before Spring Boot's default
 * error handler, ensuring every HTTP response follows RFC 9457 with our
 * contract-first {@link ProblemDetails} DTO.
 */
@Slf4j
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // If the response is already committed (partially written), we can't replace it
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = resolveStatus(ex);
        log.error("Framework-level error on {} [{}]: {}",
                exchange.getRequest().getPath(), status.value(), ex.getMessage());

        ProblemDetails problem = new ProblemDetails();
        problem.setType(URI.create("https://promptly.dev/errors/" + status.value()));
        problem.setTitle(status.getReasonPhrase());
        problem.setStatus(status.value());
        problem.setDetail(ex.getMessage());
        problem.setCode(ErrorCode.INTERNAL_ERROR);
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        problem.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(problem);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ProblemDetails", e);
            return Mono.error(ex);
        }
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            HttpStatus resolved = HttpStatus.resolve(rse.getStatusCode().value());
            return resolved != null ? resolved : HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
