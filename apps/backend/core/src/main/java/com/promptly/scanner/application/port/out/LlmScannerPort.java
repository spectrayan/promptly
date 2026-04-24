package com.promptly.scanner.application.port.out;

import com.promptly.scanner.domain.model.ScanResult;

/**
 * Outbound port for LLM-based vulnerability scanning.
 * Implemented by the Spring AI adapter in the infrastructure layer.
 */
public interface LlmScannerPort {

    /**
     * Analyzes prompt content for vulnerabilities using an LLM.
     * Runs on a virtual thread — blocking call is acceptable.
     */
    ScanResult analyzePrompt(String promptId, int version, String content);

}
