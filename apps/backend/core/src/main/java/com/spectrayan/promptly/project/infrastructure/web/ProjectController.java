package com.spectrayan.promptly.project.infrastructure.web;

import com.spectrayan.promptly.infrastructure.in.web.api.ProjectsApi;
import com.spectrayan.promptly.infrastructure.in.web.dto.*;
import com.spectrayan.promptly.project.application.port.in.CreateProjectUseCase;
import com.spectrayan.promptly.project.application.port.in.GetProjectUseCase;
import com.spectrayan.promptly.project.application.port.in.ManageProjectMembersUseCase;
import com.spectrayan.promptly.project.domain.model.Project;
import com.spectrayan.promptly.project.domain.model.ProjectMember;
import com.spectrayan.promptly.auth.application.port.in.ManageApiKeysUseCase;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
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

/**
 * REST controller for the Project & RBAC module.
 * Implements the contract-first {@link ProjectsApi} interface generated from the OpenAPI specification.
 * <p>
 * Projects are the top-level security boundary — every prompt, workflow, and scan
 * is scoped to a project. This controller handles project CRUD, member management,
 * and project API keys for Runtime Delivery.
 *
 * @see com.spectrayan.promptly.project.domain.model.Project
 * @see com.spectrayan.promptly.project.domain.model.ProjectRole
 */
@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectsApi {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectUseCase getProjectUseCase;
    private final ManageProjectMembersUseCase manageMembersUseCase;
    private final ManageApiKeysUseCase manageApiKeysUseCase;
    private final com.spectrayan.promptly.auth.application.port.in.UserQueryUseCase userQueryUseCase;

    @Override
    public Mono<ResponseEntity<Flux<ProjectResponse>>> listProjects(
            Integer page, Integer size, String sort, ServerWebExchange exchange) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 20;
        return getCurrentUserId()
                .flatMap(userId -> getProjectUseCase.listProjects(userId)
                        .map(this::toResponse)
                        .skip((long) p * s)
                        .take(s)
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
                                com.spectrayan.promptly.project.domain.model.ProjectRole.valueOf(req.getRole().name()), userId))
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
                        com.spectrayan.promptly.project.domain.model.ProjectRole.valueOf(req.getRole().name())))
        ).flatMap(this::toMemberResponseAsync)
         .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> removeProjectMember(
            String projectId, String userId, ServerWebExchange exchange) {
        return manageMembersUseCase.removeMember(projectId, userId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<ApiKeyCreatedResponse>> createProjectApiKey(
            String projectId, Mono<CreateApiKeyRequest> request, ServerWebExchange exchange) {
        return getCurrentUserId()
                .flatMap(userId -> request.flatMap(req ->
                        manageApiKeysUseCase.createApiKey(projectId, req.getName(), req.getExpiresInDays(), userId)))
                .map(gen -> {
                    var k = gen.apiKey();
                    var resp = new ApiKeyCreatedResponse(
                            k.getId(),
                            k.getProjectId(),
                            k.getName(),
                            k.getKeyPrefix(),
                            gen.rawKey(),
                            k.isRevoked(),
                            k.getCreatedAt() != null ? OffsetDateTime.ofInstant(k.getCreatedAt(), ZoneOffset.UTC) : OffsetDateTime.now()
                    );
                    if (k.getExpiresAt() != null) {
                        resp.setExpiresAt(OffsetDateTime.ofInstant(k.getExpiresAt(), ZoneOffset.UTC));
                    }
                    resp.setCreatedBy(k.getCreatedBy());
                    return ResponseEntity.status(HttpStatus.CREATED).body(resp);
                });
    }

    @Override
    public Mono<ResponseEntity<Flux<ApiKeyResponse>>> listProjectApiKeys(
            String projectId, ServerWebExchange exchange) {
        Flux<ApiKeyResponse> flux = manageApiKeysUseCase.listApiKeys(projectId)
                .map(this::toApiKeyResponse);
        return Mono.just(ResponseEntity.ok(flux));
    }

    @Override
    public Mono<ResponseEntity<Void>> revokeProjectApiKey(
            String projectId, String keyId, ServerWebExchange exchange) {
        return manageApiKeysUseCase.revokeApiKey(projectId, keyId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    private ApiKeyResponse toApiKeyResponse(ApiKey k) {
        var resp = new ApiKeyResponse(
                k.getId(),
                k.getProjectId(),
                k.getName(),
                k.getKeyPrefix(),
                k.isRevoked(),
                k.getCreatedAt() != null ? OffsetDateTime.ofInstant(k.getCreatedAt(), ZoneOffset.UTC) : OffsetDateTime.now()
        );
        if (k.getExpiresAt() != null) {
            resp.setExpiresAt(OffsetDateTime.ofInstant(k.getExpiresAt(), ZoneOffset.UTC));
        }
        if (k.getLastUsedAt() != null) {
            resp.setLastUsedAt(OffsetDateTime.ofInstant(k.getLastUsedAt(), ZoneOffset.UTC));
        }
        if (k.getRevokedAt() != null) {
            resp.setRevokedAt(OffsetDateTime.ofInstant(k.getRevokedAt(), ZoneOffset.UTC));
        }
        resp.setCreatedBy(k.getCreatedBy());
        return resp;
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
