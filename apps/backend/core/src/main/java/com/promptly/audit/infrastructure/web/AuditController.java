package com.promptly.audit.infrastructure.web;

import com.promptly.infrastructure.in.web.api.AuditApi;
import com.promptly.infrastructure.in.web.dto.AuditResponse;
import com.promptly.audit.application.port.out.AuditRepository;
import com.promptly.audit.domain.model.AuditEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * REST controller for the Audit & Compliance module.
 * Implements the contract-first {@link AuditApi} interface generated from the OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
public class AuditController implements AuditApi {

    private final AuditRepository auditRepository;

    @Override
    public Mono<ResponseEntity<Flux<AuditResponse>>> getAuditLogs(
            String projectId, String resourceId, String userId, String action, ServerWebExchange exchange) {

        Flux<AuditEntry> entries;

        if (projectId != null && !projectId.isBlank()) {
            entries = auditRepository.findByProjectId(projectId);
        } else if (resourceId != null) {
            entries = auditRepository.findByResourceId(resourceId);
        } else if (userId != null) {
            entries = auditRepository.findByActorUserId(userId);
        } else if (action != null) {
            entries = auditRepository.findByAction(action);
        } else {
            entries = auditRepository.findAll();
        }

        return Mono.just(ResponseEntity.ok(entries.map(this::toResponse)));
    }

    @Override
    public Mono<ResponseEntity<Flux<AuditResponse>>> exportAuditLogs(
            String resourceId, ServerWebExchange exchange) {
        Flux<AuditEntry> entries = (resourceId != null)
                ? auditRepository.findByResourceId(resourceId)
                : auditRepository.findAll();
        return Mono.just(ResponseEntity.ok(entries.map(this::toResponse)));
    }

    // ── Domain → DTO mapping ──────────────────────────────────────────

    private AuditResponse toResponse(AuditEntry entry) {
        var response = new AuditResponse();
        response.setId(entry.getId());
        response.setAction(entry.getAction());
        response.setResourceType(entry.getResourceType());
        response.setResourceId(entry.getResourceId());
        response.setResourceVersion(entry.getResourceVersion());
        response.setActorUserId(entry.getActorUserId());
        response.setTimestamp(toOffsetDateTime(entry.getTimestamp()));
        return response;
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

}
