package com.promptly.auth.application.port.in;

import com.promptly.auth.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port for user queries (used by project member picker).
 */
public interface UserQueryUseCase {

    Flux<User> searchUsers(String query, int limit);

    Mono<User> getUserById(String id);
}
