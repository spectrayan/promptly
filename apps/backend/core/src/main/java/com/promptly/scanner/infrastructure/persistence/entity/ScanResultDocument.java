package com.promptly.scanner.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB document for scan results.
 */
@Data
@Builder
@Document(collection = "scan_results")
@CompoundIndex(name = "idx_prompt_version", def = "{'promptId': 1, 'promptVersion': -1}")
public class ScanResultDocument {

    @Id
    private String id;

    private String promptId;
    private int promptVersion;
    private double overallScore;
    private String status;
    private String llmProvider;
    private String llmModel;
    private String scannedBy;
    private Instant scannedAt;
    private List<FindingSubdocument> findings;

    @CreatedDate
    private Instant createdAt;

    @Data
    @Builder
    public static class FindingSubdocument {
        private String type;
        private String severity;
        private String title;
        private String description;
        private String remediation;
    }

}
