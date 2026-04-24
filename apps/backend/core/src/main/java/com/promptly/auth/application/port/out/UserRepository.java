package com.promptly.auth.application.port.out;

import com.promptly.auth.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for user persistence.
 */
public interface UserRepository {

    Mono<User> save(User user);

    Mono<User> findById(String id);

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Long> count();

    Flux<User> searchByEmailOrName(String query, int limit);
}
