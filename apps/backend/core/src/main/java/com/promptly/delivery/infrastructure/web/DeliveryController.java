package com.promptly.delivery.infrastructure.web;

import com.promptly.infrastructure.in.web.api.DeliveryApi;
import com.promptly.infrastructure.in.web.dto.DeliveryResponse;
import com.promptly.delivery.application.port.in.DeliverPromptUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * REST controller for the Runtime Delivery API.
 * Implements the contract-first {@link DeliveryApi} interface generated from the OpenAPI specification.
 * Designed for low-latency prompt fetching by AI agents.
 */
@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliverPromptUseCase deliverPromptUseCase;

    @Override
    public Mono<ResponseEntity<DeliveryResponse>> deliverPrompt(
            String appId, String usecase, String agent, ServerWebExchange exchange) {
        return deliverPromptUseCase.deliverPrompt(appId, usecase, agent)
                .map(r -> {
                    var response = new DeliveryResponse();
                    response.setPromptId(r.promptId());
                    response.setName(r.name());
                    response.setContent(r.content());
                    response.setVersion(r.version());
                    response.setContentFormat(r.contentFormat());
                    return response;
                })
                .map(ResponseEntity::ok);
    }

}
