package com.promptly.scanner.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A single finding from a vulnerability scan.
 */
@Getter
@Setter
@Builder
public class Finding {

    private String type; // phi_exposure, injection_risk, missing_guardrail, hallucination_prone, weak_tool_calling
    private Severity severity;
    private String title;
    private String description;
    private String remediation;

}
