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

                    return projectRepository.save(project);
                })
                .flatMap(project -> {
                    // Creator auto-becomes ADMIN
                    ProjectMember admin = ProjectMember.builder()
                            .projectId(project.getId())
                            .userId(command.createdBy())
                            .role(ProjectRole.ADMIN)
                            .joinedAt(Instant.now())
                            .build();

                    return memberRepository.save(admin).thenReturn(project);
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
    public Mono<ProjectMember> addMember(String projectId, String userId, ProjectRole role) {
        return memberRepository.existsByProjectIdAndUserId(projectId, userId)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("User is already a member of this project"));
                    }

                    ProjectMember member = ProjectMember.builder()
                            .projectId(projectId)
                            .userId(userId)
                            .role(role)
                            .joinedAt(Instant.now())
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
}
