package com.spectrayan.promptly.prompt.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link PromptSpecifications}.
 * <p>
 * Exercises each leaf specification and composite business rule directly
 * to supplement the indirect coverage in {@link PromptTest}.
 */
@DisplayName("PromptSpecifications")
class PromptSpecificationsTest {

    // ── Fixtures ─────────────────────────────────────────────────

    private static Prompt promptWith(PromptStatus status, int version) {
        Prompt p = Prompt.builder()
                .id("p-1")
                .name("Test")
                .description("desc")
                .projectId("proj-1")
                .contentFormat(ContentFormat.TEXT)
                .tags(Set.of())
                .status(status)
                .currentVersion(version)
                .versions(new ArrayList<>())
                .build();
        // Backfill version list to match currentVersion count
        for (int i = 0; i < version; i++) {
            p.getVersions().add(PromptVersion.builder()
                    .versionNumber(i + 1).content("v" + (i + 1))
                    .changeMessage("msg").createdBy("alice").build());
        }
        return p;
    }

    // ═══════════════════════════════════════════════════════════════
    // isDraft
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isDraft")
    class IsDraftTests {
        @Test void draftSatisfied() {
            assertThat(PromptSpecifications.isDraft().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 0))).isTrue();
        }

        @Test void inReviewNotSatisfied() {
            assertThat(PromptSpecifications.isDraft().isSatisfiedBy(promptWith(PromptStatus.IN_REVIEW, 1))).isFalse();
        }

        @Test void approvedNotSatisfied() {
            assertThat(PromptSpecifications.isDraft().isSatisfiedBy(promptWith(PromptStatus.APPROVED, 1))).isFalse();
        }

        @Test void unsatisfiedReasonContainsActualStatus() {
            Prompt p = promptWith(PromptStatus.APPROVED, 1);
            assertThat(PromptSpecifications.isDraft().unsatisfiedReason(p)).contains("APPROVED");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // isNotInReview
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isNotInReview")
    class IsNotInReviewTests {
        @Test void draftSatisfied() {
            assertThat(PromptSpecifications.isNotInReview().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 0))).isTrue();
        }

        @Test void inReviewNotSatisfied() {
            assertThat(PromptSpecifications.isNotInReview().isSatisfiedBy(promptWith(PromptStatus.IN_REVIEW, 1))).isFalse();
        }

        @Test void unsatisfiedReasonMentionsReview() {
            Prompt p = promptWith(PromptStatus.IN_REVIEW, 1);
            assertThat(PromptSpecifications.isNotInReview().unsatisfiedReason(p)).contains("under review");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // hasAtLeastOneVersion / hasMultipleVersions
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("version-count specs")
    class VersionCountTests {
        @Test void zeroVersionsFailsHasAtLeastOne() {
            assertThat(PromptSpecifications.hasAtLeastOneVersion().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 0))).isFalse();
        }

        @Test void oneVersionPassesHasAtLeastOne() {
            assertThat(PromptSpecifications.hasAtLeastOneVersion().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 1))).isTrue();
        }

        @Test void oneVersionFailsHasMultiple() {
            assertThat(PromptSpecifications.hasMultipleVersions().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 1))).isFalse();
        }

        @Test void twoVersionsPassesHasMultiple() {
            assertThat(PromptSpecifications.hasMultipleVersions().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 2))).isTrue();
        }

        @Test void hasMultipleVersionsUnsatisfiedReason() {
            Prompt p = promptWith(PromptStatus.DRAFT, 1);
            assertThat(PromptSpecifications.hasMultipleVersions().unsatisfiedReason(p)).contains("only one version");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Composite specs
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("composite business rules")
    class CompositeTests {

        @Test @DisplayName("DRAFT with 1 version is editable")
        void draftEditable() {
            assertThat(PromptSpecifications.isEditable().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 1))).isTrue();
        }

        @Test @DisplayName("IN_REVIEW is not editable")
        void inReviewNotEditable() {
            assertThat(PromptSpecifications.isEditable().isSatisfiedBy(promptWith(PromptStatus.IN_REVIEW, 1))).isFalse();
        }

        @Test @DisplayName("DRAFT with 1 version is submittable")
        void draftSubmittable() {
            assertThat(PromptSpecifications.isSubmittable().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 1))).isTrue();
        }

        @Test @DisplayName("DRAFT with 0 versions is NOT submittable")
        void draftNoVersionsNotSubmittable() {
            assertThat(PromptSpecifications.isSubmittable().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 0))).isFalse();
        }

        @Test @DisplayName("DRAFT with 2 versions is rollbackable")
        void draftRollbackable() {
            assertThat(PromptSpecifications.isRollbackable().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 2))).isTrue();
        }

        @Test @DisplayName("DRAFT with 1 version is NOT rollbackable")
        void draftNotRollbackable() {
            assertThat(PromptSpecifications.isRollbackable().isSatisfiedBy(promptWith(PromptStatus.DRAFT, 1))).isFalse();
        }

        @Test @DisplayName("any prompt is clonable")
        void alwaysClonable() {
            assertThat(PromptSpecifications.isClonable().isSatisfiedBy(promptWith(PromptStatus.APPROVED, 1))).isTrue();
        }

        @Test @DisplayName("any prompt is scannable")
        void alwaysScannable() {
            assertThat(PromptSpecifications.isScannable().isSatisfiedBy(promptWith(PromptStatus.REJECTED, 1))).isTrue();
        }
    }
}
