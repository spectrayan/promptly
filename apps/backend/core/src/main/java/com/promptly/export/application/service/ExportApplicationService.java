package com.promptly.export.application.service;

import com.promptly.export.domain.model.ExportManifest;
import com.promptly.export.domain.model.PromptBundle;
import com.promptly.prompt.PromptModuleApi;
import com.promptly.prompt.PromptProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

/**
 * Export application service — assembles prompt bundles into export manifests
 * with integrity checksums and cursor support for incremental exports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportApplicationService {

    private final PromptModuleApi promptModuleApi;

    private static final String MANIFEST_VERSION = "1.0";

    /**
     * Export a single prompt by ID (latest version).
     */
    public Mono<ExportManifest> exportPrompt(String promptId, String exportedBy) {
        return promptModuleApi.findById(promptId)
                .map(prompt -> {
                    List<PromptBundle> bundles = List.of(toBundle(prompt));
                    return buildManifest(bundles, exportedBy,
                            ExportManifest.ExportFilters.builder()
                                    .promptIds(List.of(promptId))
                                    .build());
                });
    }

    /**
     * Export all prompts in a project, optionally filtered by date range.
     */
    public Mono<ExportManifest> exportByProject(String projectId, Instant approvedAfter,
                                                 Instant approvedBefore, String exportedBy) {
        return promptModuleApi.findByProjectId(projectId)
                .filter(prompt -> matchesDateRange(prompt, approvedAfter, approvedBefore))
                .map(this::toBundle)
                .collectList()
                .map(bundles -> {
                    var filters = ExportManifest.ExportFilters.builder()
                            .projectId(projectId)
                            .approvedAfter(approvedAfter)
                            .approvedBefore(approvedBefore)
                            .build();
                    return buildManifest(bundles, exportedBy, filters);
                });
    }

    /**
     * Import prompts from an export manifest into the current environment.
     * Validates checksum integrity before applying.
     */
    public Mono<ImportResult> importManifest(ExportManifest manifest) {
        // Validate checksum
        String expectedChecksum = computeChecksum(manifest.getPrompts());
        if (manifest.getChecksum() != null && !manifest.getChecksum().equals(expectedChecksum)) {
            return Mono.error(new IllegalArgumentException(
                    "Checksum mismatch — manifest may have been tampered with."));
        }

        log.info("Importing manifest with {} prompts", manifest.getPrompts().size());

        return reactor.core.publisher.Flux.fromIterable(manifest.getPrompts())
                .flatMap(bundle -> promptModuleApi.updatePrompt(
                                bundle.getPromptId(),
                                bundle.getContent(),
                                "Imported from manifest v" + manifest.getManifestVersion(),
                                manifest.getExportedBy() != null ? manifest.getExportedBy() : "import"
                        )
                        .map(p -> bundle.getPromptId())
                        .onErrorResume(e -> {
                            log.warn("Failed to import prompt {}: {}", bundle.getPromptId(), e.getMessage());
                            return Mono.empty();
                        })
                )
                .collectList()
                .map(imported -> new ImportResult(
                        imported.size(),
                        manifest.getPrompts().size() - imported.size(),
                        imported
                ));
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private PromptBundle toBundle(PromptProjection prompt) {
        return PromptBundle.builder()
                .promptId(prompt.id())
                .name(prompt.name())
                .version(prompt.currentVersion())
                .content(prompt.latestContent() != null ? prompt.latestContent() : "")
                .contentFormat(prompt.contentFormat() != null ? prompt.contentFormat() : "TEXT")
                .projectId(prompt.projectId())
                .metadata(Map.of(
                        "description", prompt.description() != null ? prompt.description() : ""
                ))
                .build();
    }

    private boolean matchesDateRange(PromptProjection prompt, Instant after, Instant before) {
        if (after != null && prompt.updatedAt() != null && prompt.updatedAt().isBefore(after)) {
            return false;
        }
        if (before != null && prompt.updatedAt() != null && prompt.updatedAt().isAfter(before)) {
            return false;
        }
        return true;
    }

    private ExportManifest buildManifest(List<PromptBundle> bundles, String exportedBy,
                                          ExportManifest.ExportFilters filters) {
        Instant now = Instant.now();
        String cursor = Base64.getEncoder().encodeToString(
                ("{\"ts\":\"" + now + "\",\"count\":" + bundles.size() + "}")
                        .getBytes(StandardCharsets.UTF_8));

        return ExportManifest.builder()
                .manifestVersion(MANIFEST_VERSION)
                .exportedAt(now)
                .exportedBy(exportedBy)
                .filters(filters)
                .cursor(cursor)
                .prompts(bundles)
                .summary(ExportManifest.ExportSummary.builder()
                        .totalPrompts(bundles.size())
                        .projectId(filters.getProjectId())
                        .build())
                .checksum(computeChecksum(bundles))
                .build();
    }

    private String computeChecksum(List<PromptBundle> bundles) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            bundles.stream()
                    .sorted(Comparator.comparing(PromptBundle::getPromptId))
                    .forEach(b -> {
                        digest.update(b.getPromptId().getBytes(StandardCharsets.UTF_8));
                        digest.update(String.valueOf(b.getVersion()).getBytes(StandardCharsets.UTF_8));
                        if (b.getContent() != null) {
                            digest.update(b.getContent().getBytes(StandardCharsets.UTF_8));
                        }
                    });
            return "sha256:" + HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            log.warn("Failed to compute checksum: {}", e.getMessage());
            return null;
        }
    }

    public record ImportResult(int imported, int failed, List<String> importedPromptIds) {}
}
