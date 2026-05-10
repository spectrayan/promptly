package com.spectrayan.promptly.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Specification} generic combinator logic.
 * Uses a simple integer domain to exercise and/or/not composition.
 */
@DisplayName("Specification combinator")
class SpecificationTest {

    // ── Leaf specifications ──────────────────────────────────────

    private static final Specification<Integer> IS_POSITIVE = new Specification<>() {
        @Override public boolean isSatisfiedBy(Integer n) { return n > 0; }
        @Override public String unsatisfiedReason(Integer n) { return n + " is not positive"; }
    };

    private static final Specification<Integer> IS_EVEN = new Specification<>() {
        @Override public boolean isSatisfiedBy(Integer n) { return n % 2 == 0; }
        @Override public String unsatisfiedReason(Integer n) { return n + " is not even"; }
    };

    // ═══════════════════════════════════════════════════════════════
    // isSatisfiedBy
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("leaf spec should evaluate correctly")
    void leafSpec() {
        assertThat(IS_POSITIVE.isSatisfiedBy(5)).isTrue();
        assertThat(IS_POSITIVE.isSatisfiedBy(-1)).isFalse();
    }

    // ═══════════════════════════════════════════════════════════════
    // and()
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("and()")
    class AndTests {

        private final Specification<Integer> positiveAndEven = IS_POSITIVE.and(IS_EVEN);

        @Test
        @DisplayName("should satisfy when both specs pass")
        void bothPass() {
            assertThat(positiveAndEven.isSatisfiedBy(4)).isTrue();
        }

        @Test
        @DisplayName("should fail when left spec fails")
        void leftFails() {
            assertThat(positiveAndEven.isSatisfiedBy(-2)).isFalse();
        }

        @Test
        @DisplayName("should fail when right spec fails")
        void rightFails() {
            assertThat(positiveAndEven.isSatisfiedBy(3)).isFalse();
        }

        @Test
        @DisplayName("should return left reason when left fails")
        void leftReason() {
            String reason = positiveAndEven.unsatisfiedReason(-2);
            assertThat(reason).contains("not positive");
        }

        @Test
        @DisplayName("should return right reason when only right fails")
        void rightReason() {
            String reason = positiveAndEven.unsatisfiedReason(3);
            assertThat(reason).contains("not even");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // or()
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("or()")
    class OrTests {

        private final Specification<Integer> positiveOrEven = IS_POSITIVE.or(IS_EVEN);

        @Test
        @DisplayName("should satisfy when both pass")
        void bothPass() {
            assertThat(positiveOrEven.isSatisfiedBy(4)).isTrue();
        }

        @Test
        @DisplayName("should satisfy when only left passes")
        void leftPasses() {
            assertThat(positiveOrEven.isSatisfiedBy(3)).isTrue();
        }

        @Test
        @DisplayName("should satisfy when only right passes")
        void rightPasses() {
            assertThat(positiveOrEven.isSatisfiedBy(-2)).isTrue();
        }

        @Test
        @DisplayName("should fail when neither passes")
        void neitherPasses() {
            assertThat(positiveOrEven.isSatisfiedBy(-3)).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // not()
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("not()")
    class NotTests {

        private final Specification<Integer> isNotPositive = IS_POSITIVE.not();

        @Test
        @DisplayName("should negate true to false")
        void negateTrue() {
            assertThat(isNotPositive.isSatisfiedBy(5)).isFalse();
        }

        @Test
        @DisplayName("should negate false to true")
        void negateFalse() {
            assertThat(isNotPositive.isSatisfiedBy(-1)).isTrue();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // unsatisfiedReason defaults
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("default unsatisfiedReason should return generic message")
    void defaultReason() {
        Specification<Integer> noReason = n -> n > 10;
        assertThat(noReason.unsatisfiedReason(5)).isEqualTo("Specification not satisfied");
    }
}
