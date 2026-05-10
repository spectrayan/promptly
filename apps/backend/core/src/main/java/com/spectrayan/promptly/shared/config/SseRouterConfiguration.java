package com.spectrayan.promptly.shared.config;

import com.spectrayan.sse.server.controller.SseEndpointHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Registers the SSE streaming endpoint using WebFlux functional routing.
 * <p>
 * The endpoint {@code GET /sse/{topic}} is handled by the Spectrayan SSE Server
 * library's {@link SseEndpointHandler}. Clients subscribe to a topic (e.g.
 * "project-abc123") and receive real-time events from that topic.
 * <p>
 * Authentication for SSE is handled via query parameter token since
 * EventSource API does not support custom headers.
 */
@Configuration
public class SseRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> sseRoutes(SseEndpointHandler handler) {
        return RouterFunctions.route()
                .GET("/api/v1/sse/{topic}", handler::handle)
                .build();
    }
}
