package com.spectrayan.promptly.delivery.infrastructure.web;

import com.spectrayan.promptly.infrastructure.in.web.api.DeliveryApi;
import com.spectrayan.promptly.infrastructure.in.web.dto.DeliveryResponse;
import com.spectrayan.promptly.delivery.application.port.in.DeliverPromptUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import com.spectrayan.promptly.shared.domain.event.PromptDelivered;

/**
 * REST controller for the Runtime Delivery API.
 * Implements the contract-first {@link DeliveryApi} interface generated from the OpenAPI specification.
 * Designed for low-latency prompt fetching by AI agents.
 */
@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliverPromptUseCase deliverPromptUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<ResponseEntity<DeliveryResponse>> deliverPrompt(
            String appId, String usecase, String agent, ServerWebExchange exchange) {
        return deliverPromptUseCase.deliverPrompt(appId, usecase, agent)
                .flatMap(r -> ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .defaultIfEmpty(new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")))
                        .doOnNext(auth -> {
                            boolean isApiKey = auth.getAuthorities().stream()
                                    .anyMatch(a -> "ROLE_API_KEY".equals(a.getAuthority()));
                            String authType = isApiKey ? "API_KEY" : (auth.isAuthenticated() ? "JWT" : "ANONYMOUS");
                            String actorId = auth.getName();
                            eventPublisher.publishEvent(new PromptDelivered(
                                    r.promptId(), appId, r.name(), usecase, agent, authType, actorId
                            ));
                        })
                        .thenReturn(r)
                )
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
