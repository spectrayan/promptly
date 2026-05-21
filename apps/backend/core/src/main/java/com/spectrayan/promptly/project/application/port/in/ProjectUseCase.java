package com.spectrayan.promptly.project.application.port.in;

import com.spectrayan.promptly.project.domain.model.Project;
import com.spectrayan.promptly.project.domain.model.ProjectMember;
import com.spectrayan.promptly.project.domain.model.ProjectRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port combining project lifecycle and RBAC member management use cases.
 * <p>
 * Implemented by {@link com.spectrayan.promptly.project.application.service.ProjectApplicationService}.
 * Consumed by the {@link com.spectrayan.promptly.project.infrastructure.web.ProjectController}.
 */
public interface ProjectUseCase {

    Mono<Project> createProject(CreateProjectCommand command);
    Mono<Project> getProject(String id);
    Flux<Project> listProjects(String userId);
    Mono<ProjectMember> addMember(String projectId, String userId, ProjectRole role, String addedBy);
    Flux<ProjectMember> listMembers(String projectId);
    Mono<ProjectRole> getUserRole(String projectId, String userId);
    Mono<ProjectMember> updateMember(String projectId, String userId, ProjectRole role);
    Mono<Void> removeMember(String projectId, String userId);

    record CreateProjectCommand(String name, String description, java.util.List<String> tags, String createdBy) {}
}
