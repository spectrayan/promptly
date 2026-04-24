package com.promptly.project.application.port.out;

import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectMemberRepository {
    Mono<ProjectMember> save(ProjectMember member);
    Flux<ProjectMember> findByProjectId(String projectId);
    Flux<ProjectMember> findByUserId(String userId);
    Mono<ProjectMember> findByProjectIdAndUserId(String projectId, String userId);
    Mono<Boolean> existsByProjectIdAndUserId(String projectId, String userId);
}
