package com.promptly.auth.infrastructure.persistence.r2dbc.repository;

import com.promptly.auth.infrastructure.persistence.r2dbc.entity.UserR2dbcEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code users} table.
 */
public interface UserR2dbcRepository extends R2dbcRepository<UserR2dbcEntity, String> {

    Mono<UserR2dbcEntity> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT * FROM users WHERE email ILIKE :query OR display_name ILIKE :query LIMIT :limit")
    Flux<UserR2dbcEntity> searchByEmailOrName(String query, int limit);
}
