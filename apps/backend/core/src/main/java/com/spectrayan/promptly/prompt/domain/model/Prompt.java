package com.spectrayan.promptly.prompt.domain.model;

import com.spectrayan.promptly.shared.domain.AggregateRoot;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Aggregate Root for the Prompt Registry bounded context.
 * <p>
 * Pure POJO — no framework annotations. The corresponding MongoDB document
 * is {@code PromptDocument} in the infrastructure layer.
 * <p>
 * Business rules are enforced via {@link PromptSpecifications} (Specification pattern).
 */
@Getter
@Setter
@SuperBuilder
public class Prompt extends AggregateRoot {

    private String name;
    private String description;
    private String projectId;
    private ContentFormat contentFormat;
    private Set<String> tags;
    private PromptMetadata metadata;
    private int currentVersion;

    /** Content of the current version — denormalized for fast reads. */
    private String content;

    @lombok.Builder.Default
    private PromptStatus status = PromptStatus.DRAFT;

    @lombok.Builder.Default
    private List<PromptVersion> versions = new ArrayList<>();

    // ═══════════════════════════════════════════════════════════════════
    // Domain behaviors with business rule enforcement
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Creates a new version of this prompt with the given content.
     * <p>
     * If the prompt is in DRAFT status, the current version is updated in place
     * (no version increment). Only non-DRAFT prompts create a true new version.
     *
     * @throws IllegalStateException if the prompt is not editable
     *         (currently in review)
     */
    public PromptVersion createNewVersion(String content, String changeMessage, String author) {
        assertSatisfies(PromptSpecifications.isEditable(),
                "Cannot create new version");

        // DRAFT prompts with existing content: update in place (no version bump)
        if (PromptSpecifications.isDraftWithExistingVersion().isSatisfiedBy(this)
                && !versions.isEmpty()) {
            PromptVersion existing = versions.get(versions.size() - 1);
            existing.setContent(content);
            existing.setChangeMessage(changeMessage);
            existing.setCreatedBy(author);
            this.content = content;
            return existing;
        }

        int nextVersion = currentVersion + 1;
        PromptVersion version = PromptVersion.builder()
                .versionNumber(nextVersion)
                .content(content)
                .changeMessage(changeMessage)
                .createdBy(author)
                .build();
        versions.add(version);
        currentVersion = nextVersion;
        this.content = content; // keep denormalized copy in sync
        return version;
    }

    /**
     * Marks this prompt as submitted for review.
     *
     * @throws IllegalStateException if the prompt cannot be submitted
     */
    public void submitForReview() {
        assertSatisfies(PromptSpecifications.isSubmittable(),
                "Cannot submit for review");
        this.status = PromptStatus.IN_REVIEW;
    }

    /**
     * Marks this prompt as approved (called after workflow approval).
     */
    public void markApproved() {
        this.status = PromptStatus.APPROVED;
    }

    /**
     * Resets status to DRAFT (e.g., when re-editing after rejection).
     */
    public void resetToDraft() {
        this.status = PromptStatus.DRAFT;
    }

    /**
     * Marks this prompt as rejected (called after workflow rejection).
     */
    public void markRejected() {
        this.status = PromptStatus.REJECTED;
    }

    /**
     * Validates that this prompt can be deleted.
     *
     * @throws IllegalStateException if the prompt cannot be deleted
     */
    public void assertDeletable() {
        assertSatisfies(PromptSpecifications.isDeletable(),
                "Cannot delete prompt");
    }

    /**
     * Validates that this prompt can be rolled back.
     *
     * @throws IllegalStateException if the prompt cannot be rolled back
     */
    public void assertRollbackable() {
        assertSatisfies(PromptSpecifications.isRollbackable(),
                "Cannot rollback prompt");
    }

    /**
     * Gets a specific version by number.
     */
    public PromptVersion getVersion(int versionNumber) {
        return versions.stream()
                .filter(v -> v.getVersionNumber() == versionNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Version " + versionNumber + " not found for prompt " + getId()));
    }

    // ═══════════════════════════════════════════════════════════════════
    // Query methods for UI (read-only checks via specifications)
    // ═══════════════════════════════════════════════════════════════════

    /** Returns true if this prompt can be edited (new version). */
    public boolean isEditable() {
        return PromptSpecifications.isEditable().isSatisfiedBy(this);
    }

    /** Returns true if this prompt can be deleted. */
    public boolean isDeletable() {
        return PromptSpecifications.isDeletable().isSatisfiedBy(this);
    }

    /** Returns true if this prompt can be submitted for review. */
    public boolean isSubmittable() {
        return PromptSpecifications.isSubmittable().isSatisfiedBy(this);
    }

    /** Returns true if this prompt can be rolled back. */
    public boolean isRollbackable() {
        return PromptSpecifications.isRollbackable().isSatisfiedBy(this);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Internal helpers
    // ═══════════════════════════════════════════════════════════════════

    private void assertSatisfies(
            com.spectrayan.promptly.shared.domain.Specification<Prompt> spec, String context) {
        if (!spec.isSatisfiedBy(this)) {
            throw new IllegalStateException(
                    context + ": " + spec.unsatisfiedReason(this));
        }
    }
}
