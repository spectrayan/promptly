package com.promptly.prompt.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Value object holding metadata about a prompt's intended usage.
 */
@Getter
@Setter
@Builder
public class PromptMetadata {

    private String model;
    private Double temperature;
    private Integer maxTokens;
    private String systemContext;

}
