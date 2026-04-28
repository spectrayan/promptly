package com.promptly.project.application.service;

import com.promptly.project.application.port.in.CreateProjectUseCase.CreateProjectCommand;
import com.promptly.project.application.port.in.ManageProjectMembersUseCase.AddMemberCommand;
import com.promptly.project.application.port.in.ManageProjectMembersUseCase.UpdateMemberCommand;
import com.promptly.project.application.port.out.ProjectMemberPersistencePort;
import com.promptly.project.application.port.out.ProjectPersistencePort;
import com.promptly.project.domain.model.Project;
import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import com.promptly.shared.exception.DuplicateResourceException;
import com.promptly.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProjectApplicationService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectApplicationService")
class ProjectApplicationServiceTest {

    @Mock private ProjectPersistencePort projectRepository;
    @Mock private ProjectMemberPersistencePort memberRepository;

    private ProjectApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ProjectApplicationService(projectRepository, memberRepository);
    }

    // ═══════════════════════════════════════════════════════════════
    // createProject
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createProject")
    class CreateProjectTests {

        @Test
        @DisplayName("should create project and auto-assign OWNER membership")
        void shouldCreateWithOwner() {
            when(projectRepository.existsByName("My Project")).thenReturn(Mono.just(false));
            when(projectRepository.save(any(Project.class)))
                    .thenAnswer(inv -> {
                        Project p = inv.getArgument(0);
                        p.setId("proj-gen");
                        return Mono.just(p);
                    });
            when(memberRepository.save(any(ProjectMember.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            var cmd = new CreateProjectCommand("My Project", "Description", List.of("tag1"), "user-1");

            StepVerifier.create(service.createProject(cmd))
                    .assertNext(project -> {
                        assertThat(project.getId()).isEqualTo("proj-gen");
                        assertThat(project.getName()).isEqualTo("My Project");
                        assertThat(project.getCreatedBy()).isEqualTo("user-1");
                    })
                    .verifyComplete();

            // Verify OWNER membership was created
            ArgumentCaptor<ProjectMember> memberCaptor = ArgumentCaptor.forClass(ProjectMember.class);
            verify(memberRepository).save(memberCaptor.capture());
            assertThat(memberCaptor.getValue().getRole()).isEqualTo(ProjectRole.OWNER);
            assertThat(memberCaptor.getValue().getUserId()).isEqualTo("user-1");
        }

        @Test
        @DisplayName("should reject duplicate project name")
        void shouldRejectDuplicate() {
            when(projectRepository.existsByName("Existing")).thenReturn(Mono.just(true));

            var cmd = new CreateProjectCommand("Existing", "Desc", List.of(), "user-1");

            StepVerifier.create(service.createProject(cmd))
                    .expectError(DuplicateResourceException.class)
                    .verify();

            verify(projectRepository, never()).save(any());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // getProject
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getProject")
    class GetProjectTests {

        @Test
        @DisplayName("should return project by id")
        void shouldReturnProject() {
            Project p = Project.builder().name("Test").createdBy("u1").createdAt(Instant.now()).build();
            p.setId("proj-1");
            when(projectRepository.findById("proj-1")).thenReturn(Mono.just(p));

            StepVerifier.create(service.getProject("proj-1"))
                    .assertNext(project -> assertThat(project.getName()).isEqualTo("Test"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException")
        void shouldThrowNotFound() {
            when(projectRepository.findById("missing")).thenReturn(Mono.empty());

            StepVerifier.create(service.getProject("missing"))
                    .expectError(ResourceNotFoundException.class)
                    .verify();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // addMember
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("addMember")
    class AddMemberTests {

        @Test
        @DisplayName("should add new member")
        void shouldAddMember() {
            when(memberRepository.existsByProjectIdAndUserId("proj-1", "user-2"))
                    .thenReturn(Mono.just(false));
            when(memberRepository.save(any(ProjectMember.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            var cmd = new AddMemberCommand("proj-1", "user-2", ProjectRole.EDITOR, "admin-1");

            StepVerifier.create(service.addMember(cmd))
                    .assertNext(member -> {
                        assertThat(member.getUserId()).isEqualTo("user-2");
                        assertThat(member.getRole()).isEqualTo(ProjectRole.EDITOR);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should reject duplicate member")
        void shouldRejectDuplicate() {
            when(memberRepository.existsByProjectIdAndUserId("proj-1", "user-2"))
                    .thenReturn(Mono.just(true));

            var cmd = new AddMemberCommand("proj-1", "user-2", ProjectRole.EDITOR, "admin-1");

            StepVerifier.create(service.addMember(cmd))
                    .expectError(DuplicateResourceException.class)
                    .verify();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // updateMember
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updateMember")
    class UpdateMemberTests {

        @Test
        @DisplayName("should update member role")
        void shouldUpdateRole() {
            ProjectMember existing = ProjectMember.builder()
                    .projectId("proj-1").userId("user-2").role(ProjectRole.VIEWER).build();
            when(memberRepository.findByProjectIdAndUserId("proj-1", "user-2"))
                    .thenReturn(Mono.just(existing));
            when(memberRepository.save(any(ProjectMember.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            StepVerifier.create(service.updateMember(
                            new UpdateMemberCommand("proj-1", "user-2", ProjectRole.EDITOR)))
                    .assertNext(member -> assertThat(member.getRole()).isEqualTo(ProjectRole.EDITOR))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should throw not found for non-existent member")
        void shouldThrowNotFound() {
            when(memberRepository.findByProjectIdAndUserId("proj-1", "nobody"))
                    .thenReturn(Mono.empty());

            StepVerifier.create(service.updateMember(
                            new UpdateMemberCommand("proj-1", "nobody", ProjectRole.EDITOR)))
                    .expectError(ResourceNotFoundException.class)
                    .verify();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // listProjects & listMembers
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("listing")
    class ListingTests {

        @Test
        @DisplayName("should list projects by user membership")
        void shouldListByUser() {
            Project p1 = Project.builder().name("P1").createdBy("u1").createdAt(Instant.now()).build();
            when(projectRepository.findByMemberUserId("user-1")).thenReturn(Flux.just(p1));

            StepVerifier.create(service.listProjects("user-1"))
                    .expectNextCount(1)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should list members for a project")
        void shouldListMembers() {
            ProjectMember m = ProjectMember.builder()
                    .projectId("proj-1").userId("u1").role(ProjectRole.OWNER).build();
            when(memberRepository.findByProjectId("proj-1")).thenReturn(Flux.just(m));

            StepVerifier.create(service.listMembers("proj-1"))
                    .expectNextCount(1)
                    .verifyComplete();
        }
    }
}
