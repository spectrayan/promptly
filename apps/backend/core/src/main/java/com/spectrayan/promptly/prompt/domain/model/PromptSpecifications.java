package com.spectrayan.promptly.prompt.domain.model;

import com.spectrayan.promptly.shared.domain.Specification;

/**
 * Domain specifications encoding the business rules for prompt lifecycle management.
 * <p>
 * Rules:
 * <ul>
 *   <li>Only DRAFT prompts can be edited, deleted, improved, rolled back, or submitted for review</li>
 *   <li>A prompt that is IN_REVIEW (has a pending workflow) cannot be edited or re-submitted</li>
 *   <li>APPROVED prompts are immutable — changes require cloning a new version</li>
 *   <li>Any prompt can be cloned regardless of status</li>
 *   <li>Any prompt can be scanned regardless of status</li>
 * </ul>
 */
public final class PromptSpecifications {

    private PromptSpecifications() {} // utility class

    // ═══════════════════════════════════════════════════════════════════
    // Status-based Specifications
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Prompt is in DRAFT status.
     */
    public static Specification<Prompt> isDraft() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(Prompt prompt) {
                return PromptStatus.DRAFT.equals(prompt.getStatus());
            }

            @Override
            public String unsatisfiedReason(Prompt prompt) {
                return "Prompt status is " + prompt.getStatus()
                        + ". Only DRAFT prompts can be modified.";
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
     * Prompt can be edited (new version created) — must be DRAFT and not in review.
     */
    public static Specification<Prompt> isEditable() {
        return isDraft().and(isNotInReview());
    }

    /**
     * Prompt can be deleted — must be DRAFT and not in review.
     */
    public static Specification<Prompt> isDeletable() {
        return isDraft().and(isNotInReview());
    }

    /**
     * Prompt can be submitted for review — must be DRAFT, not already in review,
     * and must have at least one version.
     */
    public static Specification<Prompt> isSubmittable() {
        return isDraft().and(isNotInReview()).and(hasAtLeastOneVersion());
    }

    /**
     * Prompt can be rolled back — must be DRAFT, not in review,
     * and must have more than one version.
     */
    public static Specification<Prompt> isRollbackable() {
        return isDraft().and(isNotInReview()).and(hasMultipleVersions());
    }

    /**
     * Prompt can always be cloned regardless of status.
     */
    public static Specification<Prompt> isClonable() {
        return prompt -> true; // Always allowed
    }

    /**
     * Prompt can always be scanned regardless of status.
     */
    public static Specification<Prompt> isScannable() {
        return prompt -> true; // Always allowed
    }

    /**
     * Prompt is in DRAFT status with an existing version — eligible for in-place update
     * (no version increment). Non-DRAFT prompts always create a new version.
     */
    public static Specification<Prompt> isDraftWithExistingVersion() {
        return isDraft().and(hasAtLeastOneVersion());
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
