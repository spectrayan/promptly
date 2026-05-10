package com.spectrayan.promptly.shared.domain;

/**
 * Generic Specification pattern interface.
 * <p>
 * A specification encapsulates a business rule that can be evaluated against
 * a candidate object. Specifications can be composed using {@code and},
 * {@code or}, and {@code not} to build complex rule sets.
 *
 * @param <T> the type of object this specification evaluates
 */
@FunctionalInterface
public interface Specification<T> {

    /**
     * Evaluates whether the candidate satisfies this specification.
     */
    boolean isSatisfiedBy(T candidate);

    /**
     * Returns a human-readable reason why the specification is not satisfied.
     * Default implementation returns a generic message.
     */
    default String unsatisfiedReason(T candidate) {
        return "Specification not satisfied";
    }

    /**
     * Combines this specification with another using logical AND.
     */
    default Specification<T> and(Specification<T> other) {
        return new Specification<T>() {
            @Override
            public boolean isSatisfiedBy(T candidate) {
                return Specification.this.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
            }

            @Override
            public String unsatisfiedReason(T candidate) {
                if (!Specification.this.isSatisfiedBy(candidate)) {
                    return Specification.this.unsatisfiedReason(candidate);
                }
                return other.unsatisfiedReason(candidate);
            }
        };
    }

    /**
     * Combines this specification with another using logical OR.
     */
    default Specification<T> or(Specification<T> other) {
        return candidate ->
                Specification.this.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
    }

    /**
     * Negates this specification.
     */
    default Specification<T> not() {
        return candidate -> !Specification.this.isSatisfiedBy(candidate);
    }
}
