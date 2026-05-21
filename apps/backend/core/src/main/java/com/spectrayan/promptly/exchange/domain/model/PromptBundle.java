package com.spectrayan.promptly.exchange.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * A single prompt within an export manifest.
 * Contains the prompt content, metadata, approval info, and scan results.
 */
@Data
@Builder
public class PromptBundle {

    private String promptId;
    private String name;
    private int version;
    private String content;
    private String contentFormat;
    private String projectId;
    private Map<String, Object> metadata;

    /** Approval chain proving this version was approved. */
    private ApprovalInfo approval;

    /** Latest scan results for this version. */
    private ScanInfo scan;

    @Data
    @Builder
    public static class ApprovalInfo {
        private String workflowId;
        private String approvedBy;
        private Instant approvedAt;
    }

    @Data
    @Builder
    public static class ScanInfo {
        private String status;
        private double score;
    }
}
