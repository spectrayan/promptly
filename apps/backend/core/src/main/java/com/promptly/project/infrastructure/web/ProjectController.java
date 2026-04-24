package com.promptly.project.infrastructure.web;

import com.promptly.infrastructure.in.web.api.ProjectsApi;
import com.promptly.infrastructure.in.web.dto.*;
import com.promptly.project.application.port.in.ProjectUseCase;
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

    private final ProjectUseCase projectUseCase;

    @Override
    public Mono<ResponseEntity<Flux<ProjectResponse>>> listProjects(ServerWebExchange exchange) {
        return getCurrentUserId()
                .map(userId -> ResponseEntity.ok(
                        projectUseCase.listProjects(userId).map(this::toResponse)
                ));
    }

    @Override
    public Mono<ResponseEntity<ProjectResponse>> createProject(
            Mono<CreateProjectRequest> request, ServerWebExchange exchange) {
        return getCurrentUserId()
                .flatMap(userId -> request.flatMap(req ->
                        projectUseCase.createProject(new ProjectUseCase.CreateProjectCommand(
                                req.getName(), req.getDescription(),
                                req.getTags() != null ? req.getTags() : new ArrayList<>(),
                                userId
                        ))
                ))
                .map(project -> ResponseEntity.status(HttpStatus.CREATED).body(toResponse(project)));
    }

    @Override
    public Mono<ResponseEntity<ProjectResponse>> getProject(String id, ServerWebExchange exchange) {
        return projectUseCase.getProject(id)
                .map(project -> ResponseEntity.ok(toResponse(project)));
    }

    @Override
    public Mono<ResponseEntity<Flux<ProjectMemberResponse>>> listProjectMembers(
            String projectId, ServerWebExchange exchange) {
        Flux<ProjectMemberResponse> members = projectUseCase.listMembers(projectId)
                .map(this::toMemberResponse);
        return Mono.just(ResponseEntity.ok(members));
    }

    @Override
    public Mono<ResponseEntity<ProjectMemberResponse>> addProjectMember(
            String projectId, Mono<AddMemberRequest> request, ServerWebExchange exchange) {
        return request.flatMap(req ->
                projectUseCase.addMember(projectId, req.getUserId(),
                        com.promptly.project.domain.model.ProjectRole.valueOf(req.getRole().name()))
        ).map(member -> ResponseEntity.status(HttpStatus.CREATED).body(toMemberResponse(member)));
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
        var r = new ProjectResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setTags(p.getTags());
        r.setCreatedBy(p.getCreatedBy());
        if (p.getCreatedAt() != null) r.setCreatedAt(OffsetDateTime.ofInstant(p.getCreatedAt(), ZoneOffset.UTC));
        if (p.getUpdatedAt() != null) r.setUpdatedAt(OffsetDateTime.ofInstant(p.getUpdatedAt(), ZoneOffset.UTC));
        return r;
    }

    private ProjectMemberResponse toMemberResponse(ProjectMember m) {
        var r = new ProjectMemberResponse();
        r.setUserId(m.getUserId());
        r.setDisplayName(m.getDisplayName());
        r.setEmail(m.getEmail());
        r.setRole(com.promptly.infrastructure.in.web.dto.ProjectRole.valueOf(m.getRole().name()));
        if (m.getJoinedAt() != null) r.setJoinedAt(OffsetDateTime.ofInstant(m.getJoinedAt(), ZoneOffset.UTC));
        return r;
    }
}
