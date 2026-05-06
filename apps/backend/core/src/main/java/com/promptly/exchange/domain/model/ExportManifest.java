package com.promptly.exchange.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * A self-contained export manifest containing one or more approved prompt bundles.
 * Used for CI/CD-driven environment promotion (dev→staging→prod).
 * <p>
 * Each manifest includes its own integrity checksum and a cursor for incremental exports.
 */
@Data
@Builder
public class ExportManifest {

    private String manifestVersion;
    private Instant exportedAt;
    private String exportedBy;

    /** Filters that produced this manifest. */
    private ExportFilters filters;

    /**
     * Opaque cursor token for incremental exports.
     * Pass this to the next export call to get only what's new.
     */
    private String cursor;

    /** The exported prompt bundles. */
    private List<PromptBundle> prompts;

    /** Summary statistics. */
    private ExportSummary summary;

    /** SHA-256 checksum over the sorted prompt content for integrity verification. */
    private String checksum;

    @Data
    @Builder
    public static class ExportFilters {
        private String projectId;
        private List<String> promptIds;
        private Instant approvedAfter;
        private Instant approvedBefore;
    }

    @Data
    @Builder
    public static class ExportSummary {
        private int totalPrompts;
        private String projectId;
        private String projectName;
    }
}
