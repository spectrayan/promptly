package com.promptly.prompt.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Prompt} aggregate root.
 * <p>
 * Validates versioning behavior, lifecycle transitions, and
 * Specification-based guard clauses. Pure domain tests — no Spring context.
 */
@DisplayName("Prompt Aggregate")
class PromptTest {

    // ── Test Fixtures ────────────────────────────────────────────────

    private static Prompt draftPrompt() {
        return Prompt.builder()
                .id("p-1")
                .name("Test Prompt")
                .description("A test prompt")
                .projectId("proj-1")
                .contentFormat(ContentFormat.TEXT)
                .tags(Set.of("test"))
                .status(PromptStatus.DRAFT)
                .currentVersion(0)
                .versions(new ArrayList<>())
                .build();
    }

    private static Prompt draftWithOneVersion() {
        Prompt p = draftPrompt();
        p.createNewVersion("Hello World", "Initial", "alice");
        return p;
    }

    private static Prompt draftWithTwoVersions() {
        Prompt p = draftWithOneVersion();
        p.createNewVersion("Hello World v2", "Updated", "bob");
        return p;
    }

    // ═════════════════════════════════════════════════════════════════
    // Versioning
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createNewVersion")
    class VersioningTests {

        @Test
        @DisplayName("should create first version correctly")
        void shouldCreateFirstVersion() {
            Prompt p = draftPrompt();

            PromptVersion v = p.createNewVersion("content", "initial commit", "alice");

            assertThat(v.getVersionNumber()).isEqualTo(1);
            assertThat(v.getContent()).isEqualTo("content");
            assertThat(v.getChangeMessage()).isEqualTo("initial commit");
            assertThat(v.getCreatedBy()).isEqualTo("alice");
            assertThat(p.getCurrentVersion()).isEqualTo(1);
            assertThat(p.getVersions()).hasSize(1);
        }

        @Test
        @DisplayName("should increment version number sequentially")
        void shouldIncrementVersions() {
            Prompt p = draftPrompt();

            p.createNewVersion("v1", "first", "alice");
            p.createNewVersion("v2", "second", "bob");
            PromptVersion v3 = p.createNewVersion("v3", "third", "carol");

            assertThat(v3.getVersionNumber()).isEqualTo(3);
            assertThat(p.getCurrentVersion()).isEqualTo(3);
            assertThat(p.getVersions()).hasSize(3);
        }

        @Test
        @DisplayName("should throw when creating version on IN_REVIEW prompt")
        void shouldBlockVersionOnInReview() {
            Prompt p = draftWithOneVersion();
            p.submitForReview();

            assertThatThrownBy(() -> p.createNewVersion("new", "update", "bob"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot create new version");
        }

        @Test
        @DisplayName("should throw when creating version on APPROVED prompt")
        void shouldBlockVersionOnApproved() {
            Prompt p = draftWithOneVersion();
            p.markApproved();

            assertThatThrownBy(() -> p.createNewVersion("new", "update", "bob"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot create new version");
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // Lifecycle transitions
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("lifecycle transitions")
    class LifecycleTests {

        @Test
        @DisplayName("should submit DRAFT prompt for review")
        void shouldSubmitForReview() {
            Prompt p = draftWithOneVersion();

            p.submitForReview();

            assertThat(p.getStatus()).isEqualTo(PromptStatus.IN_REVIEW);
        }

        @Test
        @DisplayName("should throw when submitting prompt with no versions")
        void shouldBlockSubmitWithNoVersions() {
            Prompt p = draftPrompt();

            assertThatThrownBy(p::submitForReview)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot submit for review");
        }

        @Test
        @DisplayName("should throw when submitting already IN_REVIEW prompt")
        void shouldBlockDoubleSubmit() {
            Prompt p = draftWithOneVersion();
            p.submitForReview();

            assertThatThrownBy(p::submitForReview)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot submit for review");
        }

        @Test
        @DisplayName("should mark as approved")
        void shouldMarkApproved() {
            Prompt p = draftWithOneVersion();

            p.markApproved();

            assertThat(p.getStatus()).isEqualTo(PromptStatus.APPROVED);
        }

        @Test
        @DisplayName("should mark as rejected")
        void shouldMarkRejected() {
            Prompt p = draftWithOneVersion();

            p.markRejected();

            assertThat(p.getStatus()).isEqualTo(PromptStatus.REJECTED);
        }

        @Test
        @DisplayName("should reset to DRAFT")
        void shouldResetToDraft() {
            Prompt p = draftWithOneVersion();
            p.markRejected();

            p.resetToDraft();

            assertThat(p.getStatus()).isEqualTo(PromptStatus.DRAFT);
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // Guard clauses (assertDeletable, assertRollbackable)
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("guard clauses")
    class GuardTests {

        @Test
        @DisplayName("DRAFT prompt with no review is deletable")
        void draftIsDeletable() {
            Prompt p = draftPrompt();

            assertThatCode(p::assertDeletable).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("IN_REVIEW prompt is not deletable")
        void inReviewNotDeletable() {
            Prompt p = draftWithOneVersion();
            p.submitForReview();

            assertThatThrownBy(p::assertDeletable)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot delete prompt");
        }

        @Test
        @DisplayName("APPROVED prompt is not deletable")
        void approvedNotDeletable() {
            Prompt p = draftWithOneVersion();
            p.markApproved();

            assertThatThrownBy(p::assertDeletable)
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("prompt with 2+ versions is rollbackable")
        void multiVersionIsRollbackable() {
            Prompt p = draftWithTwoVersions();

            assertThatCode(p::assertRollbackable).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("prompt with 1 version is not rollbackable")
        void singleVersionNotRollbackable() {
            Prompt p = draftWithOneVersion();

            assertThatThrownBy(p::assertRollbackable)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot rollback prompt");
        }

        @Test
        @DisplayName("prompt with 0 versions is not rollbackable")
        void noVersionNotRollbackable() {
            Prompt p = draftPrompt();

            assertThatThrownBy(p::assertRollbackable)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // Query predicates
    // ═════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("query predicates")
    class QueryTests {

        @Test
        @DisplayName("DRAFT prompt should be editable, deletable, and not rollbackable with one version")
        void draftPredicates() {
            Prompt p = draftWithOneVersion();

            assertThat(p.isEditable()).isTrue();
            assertThat(p.isDeletable()).isTrue();
            assertThat(p.isSubmittable()).isTrue();
            assertThat(p.isRollbackable()).isFalse();
        }

        @Test
        @DisplayName("IN_REVIEW prompt should be non-editable, non-deletable, non-submittable")
        void inReviewPredicates() {
            Prompt p = draftWithOneVersion();
            p.submitForReview();

            assertThat(p.isEditable()).isFalse();
            assertThat(p.isDeletable()).isFalse();
            assertThat(p.isSubmittable()).isFalse();
        }

        @Test
        @DisplayName("getVersion should return correct version")
        void getVersionByNumber() {
            Prompt p = draftWithTwoVersions();

            PromptVersion v1 = p.getVersion(1);
            PromptVersion v2 = p.getVersion(2);

            assertThat(v1.getContent()).isEqualTo("Hello World");
            assertThat(v2.getContent()).isEqualTo("Hello World v2");
        }

        @Test
        @DisplayName("getVersion should throw for non-existent version")
        void getVersionNotFound() {
            Prompt p = draftWithOneVersion();

            assertThatThrownBy(() -> p.getVersion(999))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Version 999 not found");
        }
    }
}
