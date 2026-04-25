package com.promptly.scanner.domain.model;

import com.promptly.shared.domain.AggregateRoot;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root for vulnerability scan results.
 */
@Getter
@Setter
@SuperBuilder
public class ScanResult extends AggregateRoot {

    private String projectId;
    private String promptId;
    private int promptVersion;
    private double overallScore;
    private String status; // pass | warn | fail
    private String llmProvider;
    private String llmModel;
    private String scannedBy;
    private Instant scannedAt;

    @lombok.Builder.Default
    private List<Finding> findings = new ArrayList<>();

    /**
     * Computes the status based on the overall score.
     */
    public void computeStatus() {
        if (overallScore <= 2.0) {
            status = "pass";
        } else if (overallScore <= 5.0) {
            status = "warn";
        } else {
            status = "fail";
        }
    }

}
