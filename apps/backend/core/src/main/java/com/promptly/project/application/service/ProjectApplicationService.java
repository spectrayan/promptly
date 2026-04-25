package com.promptly.project.application.service;

import com.promptly.project.application.port.in.ProjectUseCase;
import com.promptly.project.application.port.out.ProjectMemberRepository;
import com.promptly.project.application.port.out.ProjectRepository;
import com.promptly.project.domain.model.Project;
import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectApplicationService implements ProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;

    @Override
    public Mono<Project> createProject(CreateProjectCommand command) {
        log.info("Creating project: {}", command.name());

        return projectRepository.existsByName(command.name())
                .doOnNext(exists -> log.info("existsByName result for '{}': {}", command.name(), exists))
                .doOnError(e -> log.error("existsByName error for '{}': {}", command.name(), e.getMessage()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Project name already exists: " + command.name()));
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
                    // Creator auto-becomes ADMIN
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
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Project", id)));
    }

    @Override
    public Flux<Project> listProjects(String userId) {
        return projectRepository.findByMemberUserId(userId);
    }

    @Override
    public Mono<ProjectMember> addMember(String projectId, String userId, ProjectRole role, String addedBy) {
        return memberRepository.existsByProjectIdAndUserId(projectId, userId)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("User is already a member of this project"));
                    }

                    ProjectMember member = ProjectMember.builder()
                            .projectId(projectId)
                            .userId(userId)
                            .role(role)
                            .addedBy(addedBy)
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
    public Mono<ProjectMember> updateMember(String projectId, String userId, ProjectRole role) {
        return memberRepository.findByProjectIdAndUserId(projectId, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("ProjectMember", userId)))
                .flatMap(member -> {
                    member.setRole(role);
                    return memberRepository.save(member);
                });
    }

    @Override
    public Mono<Void> removeMember(String projectId, String userId) {
        return memberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }
}
