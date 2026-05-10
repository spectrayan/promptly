package com.spectrayan.promptly.auth.infrastructure.persistence.r2dbc;

import com.spectrayan.promptly.AbstractR2dbcIntegrationTest;
import com.spectrayan.promptly.auth.application.port.out.UserPersistencePort;
import com.spectrayan.promptly.auth.domain.model.OrgRole;
import com.spectrayan.promptly.auth.domain.model.User;
import com.spectrayan.promptly.auth.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the R2DBC User adapter.
 * Verifies save, lookup by email, search, and count against PostgreSQL.
 */
@SpringBootTest
class UserR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private UserPersistencePort userPort;

    @BeforeEach
    void cleanUp() {
        userPort.searchByEmailOrName("", 1000)
                .collectList()
                .block(); // warm-up
    }

    private User buildUser(String email, String displayName) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .displayName(displayName)
                .passwordHash("$2a$10$hashedpassword")
                .avatarUrl("https://avatar.example.com/" + email)
                .orgRole(OrgRole.ORG_USER)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldSaveAndFindUserById() {
        var user = buildUser("alice@example.com", "Alice Smith");

        StepVerifier.create(
                userPort.save(user)
                        .flatMap(saved -> userPort.findById(saved.getId()))
        )
        .assertNext(found -> {
            assertThat(found.getEmail()).isEqualTo("alice@example.com");
            assertThat(found.getDisplayName()).isEqualTo("Alice Smith");
            assertThat(found.getOrgRole()).isEqualTo(OrgRole.ORG_USER);
            assertThat(found.getStatus()).isEqualTo(UserStatus.ACTIVE);
        })
        .verifyComplete();
    }

    @Test
    void shouldFindUserByEmail() {
        var user = buildUser("bob@example.com", "Bob Jones");

        StepVerifier.create(
                userPort.save(user)
                        .then(userPort.findByEmail("bob@example.com"))
        )
        .assertNext(found -> assertThat(found.getDisplayName()).isEqualTo("Bob Jones"))
        .verifyComplete();
    }

    @Test
    void shouldReturnEmptyForNonExistentEmail() {
        StepVerifier.create(userPort.findByEmail("nobody@example.com"))
                .verifyComplete();
    }

    @Test
    void shouldCheckExistsByEmail() {
        var user = buildUser("exists@example.com", "Exists User");

        StepVerifier.create(
                userPort.save(user)
                        .then(userPort.existsByEmail("exists@example.com"))
        )
        .assertNext(exists -> assertThat(exists).isTrue())
        .verifyComplete();

        StepVerifier.create(userPort.existsByEmail("nope@example.com"))
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldSearchByEmailOrName() {
        var user1 = buildUser("searchable@example.com", "Searchable User");
        var user2 = buildUser("other@example.com", "Other User");

        StepVerifier.create(
                userPort.save(user1)
                        .then(userPort.save(user2))
                        .thenMany(userPort.searchByEmailOrName("searchable", 10))
                        .collectList()
        )
        .assertNext(results -> {
            assertThat(results).hasSizeGreaterThanOrEqualTo(1);
            assertThat(results.get(0).getEmail()).containsIgnoringCase("searchable");
        })
        .verifyComplete();
    }

    @Test
    void shouldCountUsers() {
        var user = buildUser("count-" + UUID.randomUUID() + "@test.com", "Count User");

        StepVerifier.create(
                userPort.save(user).then(userPort.count())
        )
        .assertNext(count -> assertThat(count).isGreaterThanOrEqualTo(1))
        .verifyComplete();
    }
}
