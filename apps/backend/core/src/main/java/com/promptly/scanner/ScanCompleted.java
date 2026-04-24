package com.promptly.scanner;

import com.promptly.shared.domain.DomainEvent;
import java.time.Instant;

/**
 * Published when a vulnerability scan completes.
 * Consumed by: prompt (update safety profile), audit (log scan result), notifications.
 */
public record ScanCompleted(
        String aggregateId,
        String promptId,
        String projectId,
        int promptVersion,
        String status,
        double overallScore,
        int findingCount,
        Instant occurredAt
) implements DomainEvent {
    public ScanCompleted(String scanId, String promptId, String projectId,
                          int promptVersion, String status, double overallScore, int findingCount) {
        this(scanId, promptId, projectId, promptVersion, status, overallScore, findingCount, Instant.now());
    }

    /** Returns true if any findings are critical or high severity. */
    public boolean hasCriticalFindings() {
        return "fail".equalsIgnoreCase(status) || overallScore >= 7.0;
    }
}
