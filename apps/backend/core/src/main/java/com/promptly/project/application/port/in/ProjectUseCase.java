package com.promptly.project.application.port.in;

import com.promptly.project.domain.model.Project;
import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectUseCase {

    Mono<Project> createProject(CreateProjectCommand command);
    Mono<Project> getProject(String id);
    Flux<Project> listProjects(String userId);
    Mono<ProjectMember> addMember(String projectId, String userId, ProjectRole role);
    Flux<ProjectMember> listMembers(String projectId);
    Mono<ProjectRole> getUserRole(String projectId, String userId);

    record CreateProjectCommand(String name, String description, java.util.List<String> tags, String createdBy) {}
}
