package com.promptly.export.infrastructure.web;

import com.promptly.export.application.service.ExportApplicationService;
import com.promptly.export.domain.model.ExportManifest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * REST controller for prompt export/import operations.
 * Used by CI/CD pipelines and admin tools for environment promotion.
 */
@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportApplicationService exportService;

    /**
     * Export a single prompt by ID.
     */
    @GetMapping("/prompts/{promptId}")
    public Mono<ResponseEntity<ExportManifest>> exportPrompt(
            @PathVariable String promptId,
            @RequestHeader(value = "X-Exported-By", defaultValue = "api") String exportedBy) {

        return exportService.exportPrompt(promptId, exportedBy)
                .map(ResponseEntity::ok);
    }

    /**
     * Export all prompts in a project with optional date filters.
     */
    @GetMapping("/projects/{projectId}")
    public Mono<ResponseEntity<ExportManifest>> exportByProject(
            @PathVariable String projectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant approvedAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant approvedBefore,
            @RequestHeader(value = "X-Exported-By", defaultValue = "api") String exportedBy) {

        return exportService.exportByProject(projectId, approvedAfter, approvedBefore, exportedBy)
                .map(ResponseEntity::ok);
    }

    /**
     * Import prompts from an export manifest.
     * Validates checksum integrity before applying.
     */
    @PostMapping("/import")
    public Mono<ResponseEntity<Map<String, Object>>> importManifest(@RequestBody ExportManifest manifest) {
        return exportService.importManifest(manifest)
                .map(result -> ResponseEntity.ok(Map.of(
                        "status", "completed",
                        "imported", result.imported(),
                        "failed", result.failed(),
                        "importedPromptIds", result.importedPromptIds()
                )))
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.just(ResponseEntity.badRequest().body(
                                Map.of("status", "failed", "error", e.getMessage()))));
    }
}
