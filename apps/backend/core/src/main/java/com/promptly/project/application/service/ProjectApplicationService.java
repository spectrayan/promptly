package com.promptly.project.application.service;

import com.promptly.project.application.port.in.CreateProjectUseCase;
import com.promptly.project.application.port.in.GetProjectUseCase;
import com.promptly.project.application.port.in.ManageProjectMembersUseCase;
import com.promptly.project.application.port.out.ProjectMemberPersistencePort;
import com.promptly.project.application.port.out.ProjectPersistencePort;
import com.promptly.project.domain.model.Project;
import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import com.promptly.shared.exception.DuplicateResourceException;
import com.promptly.shared.exception.ErrorCode;
import com.promptly.shared.exception.ErrorMessages;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Application service orchestrating project lifecycle and RBAC membership.
 * <p>
 * Implements all project use cases: creation (with automatic OWNER membership),
 * querying, and member management (add, update role, remove).
 * Enforces uniqueness constraints on project names and member assignments.
 *
 * @see com.promptly.project.domain.model.Project
 * @see com.promptly.project.domain.model.ProjectRole
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectApplicationService implements CreateProjectUseCase, GetProjectUseCase, ManageProjectMembersUseCase {

    private final ProjectPersistencePort projectRepository;
    private final ProjectMemberPersistencePort memberRepository;

    @Override
    public Mono<Project> createProject(CreateProjectCommand command) {
        log.info("Creating project: {}", command.name());

        return projectRepository.existsByName(command.name())
                .doOnNext(exists -> log.info("existsByName result for '{}': {}", command.name(), exists))
                .doOnError(e -> log.error("existsByName error for '{}': {}", command.name(), e.getMessage()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException(
                                ErrorCode.PROJECT_DUPLICATE_NAME,
                                String.format(ErrorMessages.DUPLICATE_PROJECT_NAME, command.name())));
                    }

                    Project project = Project.builder()
                            .name(command.name())
                            .description(command.description())
                            .tags(command.tags())
                            .createdBy(command.createdBy())
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();

                    log.info("Saving project: {}", project.getName());
                    return projectRepository.save(project)
                            .doOnNext(p -> log.info("Project saved: id={}", p.getId()))
                            .doOnError(e -> log.error("Project save error: {}", e.getMessage()));
                })
                .flatMap(project -> {
                    log.info("Creating OWNER membership for project: {}", project.getId());
                    // Creator auto-becomes OWNER
                    ProjectMember admin = ProjectMember.builder()
                            .projectId(project.getId())
                            .userId(command.createdBy())
                            .role(ProjectRole.OWNER)
                            .addedBy(command.createdBy())
                            .addedAt(Instant.now())
                            .build();

                    return memberRepository.save(admin)
                            .doOnNext(m -> log.info("Member saved: id={}", m.getId()))
                            .doOnError(e -> log.error("Member save error: {}", e.getMessage()))
                            .thenReturn(project);
                });
    }

    @Override
    public Mono<Project> getProject(String id) {
        return projectRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ErrorCode.PROJECT_NOT_FOUND, "Project", id)));
    }

    @Override
    public Flux<Project> listProjects(String userId) {
        return projectRepository.findByMemberUserId(userId);
    }

    @Override
    public Mono<ProjectMember> addMember(AddMemberCommand command) {
        return memberRepository.existsByProjectIdAndUserId(command.projectId(), command.userId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException(
                                ErrorCode.PROJECT_MEMBER_EXISTS, ErrorMessages.DUPLICATE_PROJECT_MEMBER));
                    }

                    ProjectMember member = ProjectMember.builder()
                            .projectId(command.projectId())
                            .userId(command.userId())
                            .role(command.role())
                            .addedBy(command.addedBy())
                            .addedAt(Instant.now())
                            .build();

                    return memberRepository.save(member);
                });
    }

    @Override
    public Flux<ProjectMember> listMembers(String projectId) {
        return memberRepository.findByProjectId(projectId);
    }

    @Override
    public Mono<ProjectRole> getUserRole(String projectId, String userId) {
        return memberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(ProjectMember::getRole);
    }

    @Override
    public Mono<ProjectMember> updateMember(UpdateMemberCommand command) {
        return memberRepository.findByProjectIdAndUserId(command.projectId(), command.userId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("ProjectMember", command.userId())))
                .flatMap(member -> {
                    member.setRole(command.role());
                    return memberRepository.save(member);
                });
    }

    @Override
    public Mono<Void> removeMember(String projectId, String userId) {
        return memberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }
}
