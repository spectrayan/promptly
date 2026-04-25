package com.promptly.delivery.application.port.in;

import reactor.core.publisher.Mono;

/**
 * Inbound port for runtime prompt delivery.
 * Low-latency, cached endpoint for AI agents to fetch prompts.
 */
public interface DeliverPromptUseCase {

    Mono<DeliveryResponse> deliverPrompt(String appId, String usecase, String agent);

    record DeliveryResponse(
            String promptId,
            String name,
            String content,
            int version,
            String contentFormat
    ) {}

}
