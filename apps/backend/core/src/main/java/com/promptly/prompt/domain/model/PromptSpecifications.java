package com.promptly.prompt.domain.model;

import com.promptly.shared.domain.Specification;

/**
 * Domain specifications encoding the business rules for prompt lifecycle management.
 * <p>
 * Rules:
 * <ul>
 *   <li>Only DEV prompts can be edited, deleted, improved, rolled back, or submitted for review</li>
 *   <li>STAGING and PROD prompts are immutable — changes require cloning a new version</li>
 *   <li>A prompt that is IN_REVIEW (has a pending workflow) cannot be edited or re-submitted</li>
 *   <li>Any prompt can be cloned regardless of environment</li>
 *   <li>Any prompt can be scanned regardless of environment</li>
 * </ul>
 */
public final class PromptSpecifications {

    private PromptSpecifications() {} // utility class

    // ═══════════════════════════════════════════════════════════════════
    // Environment-based Specifications
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Prompt is in the DEV environment.
     */
    public static Specification<Prompt> isInDev() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Prompt prompt) {
                return "DEV".equalsIgnoreCase(prompt.getActiveEnvironment());
            }

            @Override
            public String unsatisfiedReason(Prompt prompt) {
                return "Prompt is in " + prompt.getActiveEnvironment()
                        + " environment. Only DEV prompts can be modified.";
            }
        };
    }

    /**
     * Prompt is NOT currently in review (no pending workflow).
     */
    public static Specification<Prompt> isNotInReview() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Prompt prompt) {
                return !PromptStatus.IN_REVIEW.equals(prompt.getStatus());
            }

            @Override
            public String unsatisfiedReason(Prompt prompt) {
                return "Prompt is currently under review and cannot be modified until the review is completed.";
            }
        };
    }

    // ═══════════════════════════════════════════════════════════════════
    // Composite Business Rule Specifications
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Prompt can be edited (new version created) — must be DEV and not in review.
     */
    public static Specification<Prompt> isEditable() {
        return isInDev().and(isNotInReview());
    }

    /**
     * Prompt can be deleted — must be DEV and not in review.
     */
    public static Specification<Prompt> isDeletable() {
        return isInDev().and(isNotInReview());
    }

    /**
     * Prompt can be submitted for review — must be DEV, not already in review,
     * and must have at least one version.
     */
    public static Specification<Prompt> isSubmittable() {
        return isInDev().and(isNotInReview()).and(hasAtLeastOneVersion());
    }

    /**
     * Prompt can be rolled back — must be DEV, not in review,
     * and must have more than one version.
     */
    public static Specification<Prompt> isRollbackable() {
        return isInDev().and(isNotInReview()).and(hasMultipleVersions());
    }

    /**
     * Prompt can always be cloned regardless of environment or review status.
     */
    public static Specification<Prompt> isClonable() {
        return prompt -> true; // Always allowed
    }

    /**
     * Prompt can always be scanned regardless of environment.
     */
    public static Specification<Prompt> isScannable() {
        return prompt -> true; // Always allowed
    }

    // ═══════════════════════════════════════════════════════════════════
    // Helper Specifications
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Prompt has at least one version (content exists).
     */
    public static Specification<Prompt> hasAtLeastOneVersion() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Prompt prompt) {
                return prompt.getCurrentVersion() >= 1;
            }

            @Override
            public String unsatisfiedReason(Prompt prompt) {
                return "Prompt has no content versions yet.";
            }
        };
    }

    /**
     * Prompt has more than one version (rollback target exists).
     */
    public static Specification<Prompt> hasMultipleVersions() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Prompt prompt) {
                return prompt.getCurrentVersion() > 1;
            }

            @Override
            public String unsatisfiedReason(Prompt prompt) {
                return "Prompt has only one version. Rollback requires at least two versions.";
            }
        };
    }
}
