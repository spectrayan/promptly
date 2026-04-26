package com.promptly.project.infrastructure.web;

import com.promptly.infrastructure.in.web.api.ProjectsApi;
import com.promptly.infrastructure.in.web.dto.*;
import com.promptly.project.application.port.in.CreateProjectUseCase;
import com.promptly.project.application.port.in.GetProjectUseCase;
import com.promptly.project.application.port.in.ManageProjectMembersUseCase;
import com.promptly.project.domain.model.Project;
import com.promptly.project.domain.model.ProjectMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectsApi {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectUseCase getProjectUseCase;
    private final ManageProjectMembersUseCase manageMembersUseCase;
    private final com.promptly.auth.application.port.in.UserQueryUseCase userQueryUseCase;

    @Override
    public Mono<ResponseEntity<Flux<ProjectResponse>>> listProjects(ServerWebExchange exchange) {
        return getCurrentUserId()
                .flatMap(userId -> getProjectUseCase.listProjects(userId)
                        .map(this::toResponse)
                        .collectList()
                        .map(list -> ResponseEntity.ok(Flux.fromIterable(list)))
                );
    }

    @Override
    public Mono<ResponseEntity<ProjectResponse>> createProject(
            Mono<CreateProjectRequest> request, ServerWebExchange exchange) {
        return getCurrentUserId()
                .flatMap(userId -> request.flatMap(req ->
                        createProjectUseCase.createProject(new CreateProjectUseCase.CreateProjectCommand(
                                req.getName(), req.getDescription(),
                                req.getTags() != null ? req.getTags() : new ArrayList<>(),
                                userId
                        ))
                ))
                .map(project -> ResponseEntity.status(HttpStatus.CREATED).body(toResponse(project)));
    }

    @Override
    public Mono<ResponseEntity<ProjectResponse>> getProject(String id, ServerWebExchange exchange) {
        return getProjectUseCase.getProject(id)
                .map(project -> ResponseEntity.ok(toResponse(project)));
    }

    @Override
    public Mono<ResponseEntity<Flux<ProjectMemberResponse>>> listProjectMembers(
            String projectId, ServerWebExchange exchange) {
        return manageMembersUseCase.listMembers(projectId)
                .flatMap(this::toMemberResponseAsync)
                .collectList()
                .map(list -> ResponseEntity.ok(Flux.fromIterable(list)));
    }

    @Override
    public Mono<ResponseEntity<ProjectMemberResponse>> addProjectMember(
            String projectId, Mono<AddMemberRequest> request, ServerWebExchange exchange) {
        return getCurrentUserId().flatMap(userId ->
                request.flatMap(req ->
                        manageMembersUseCase.addMember(new ManageProjectMembersUseCase.AddMemberCommand(
                                projectId, req.getUserId(),
                                com.promptly.project.domain.model.ProjectRole.valueOf(req.getRole().name()), userId))
                )
        ).flatMap(this::toMemberResponseAsync)
         .map(member -> ResponseEntity.status(HttpStatus.CREATED).body(member));
    }

    @Override
    public Mono<ResponseEntity<ProjectMemberResponse>> updateProjectMember(
            String projectId, String userId, Mono<UpdateMemberRequest> request, ServerWebExchange exchange) {
        return request.flatMap(req ->
                manageMembersUseCase.updateMember(new ManageProjectMembersUseCase.UpdateMemberCommand(
                        projectId, userId,
                        com.promptly.project.domain.model.ProjectRole.valueOf(req.getRole().name())))
        ).flatMap(this::toMemberResponseAsync)
         .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> removeProjectMember(
            String projectId, String userId, ServerWebExchange exchange) {
        return manageMembersUseCase.removeMember(projectId, userId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> {
                    var auth = ctx.getAuthentication();
                    return auth != null && auth.getPrincipal() != null
                            ? auth.getPrincipal().toString() : "anonymous";
                })
                .defaultIfEmpty("anonymous");
    }

    private ProjectResponse toResponse(Project p) {
        var r = new ProjectResponse(p.getId(), p.getName(), p.getCreatedBy(),
                p.getCreatedAt() != null ? OffsetDateTime.ofInstant(p.getCreatedAt(), ZoneOffset.UTC) : OffsetDateTime.now());
        r.setDescription(p.getDescription());
        r.setTags(p.getTags());
        if (p.getUpdatedAt() != null) r.setUpdatedAt(OffsetDateTime.ofInstant(p.getUpdatedAt(), ZoneOffset.UTC));
        return r;
    }

    private Mono<ProjectMemberResponse> toMemberResponseAsync(ProjectMember m) {
        return userQueryUseCase.getUserById(m.getUserId())
                .map(user -> {
                    var r = new ProjectMemberResponse(m.getUserId(), user.getDisplayName(), user.getEmail(),
                            ProjectRole.fromValue(m.getRole().name()));
                    if (m.getAddedAt() != null) r.setAddedAt(OffsetDateTime.ofInstant(m.getAddedAt(), ZoneOffset.UTC));
                    r.setAddedBy(m.getAddedBy());
                    return r;
                })
                .defaultIfEmpty(new ProjectMemberResponse(m.getUserId(), "Unknown User", "unknown@example.com",
                        ProjectRole.fromValue(m.getRole().name())));
    }
}
