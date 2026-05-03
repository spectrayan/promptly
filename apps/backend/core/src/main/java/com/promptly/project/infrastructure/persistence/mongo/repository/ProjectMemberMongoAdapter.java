package com.promptly.project.infrastructure.persistence.mongo.repository;

import com.promptly.project.application.port.out.ProjectMemberPersistencePort;
import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import com.promptly.project.infrastructure.persistence.mongo.entity.ProjectMemberDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class ProjectMemberMongoAdapter implements ProjectMemberPersistencePort {

    private final ProjectMemberReactiveMongoRepository mongoRepo;

    @Override
    public Mono<ProjectMember> save(ProjectMember member) {
        return mongoRepo.save(toDocument(member)).map(this::toDomain);
    }

    @Override
    public Flux<ProjectMember> findByProjectId(String projectId) {
        return mongoRepo.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Flux<ProjectMember> findByUserId(String userId) {
        return mongoRepo.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Mono<ProjectMember> findByProjectIdAndUserId(String projectId, String userId) {
        return mongoRepo.findByProjectIdAndUserId(projectId, userId).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByProjectIdAndUserId(String projectId, String userId) {
        return mongoRepo.existsByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public Mono<Void> deleteByProjectIdAndUserId(String projectId, String userId) {
        return mongoRepo.deleteByProjectIdAndUserId(projectId, userId);
    }

    private ProjectMember toDomain(ProjectMemberDocument doc) {
        return ProjectMember.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .userId(doc.getUserId())
                .role(ProjectRole.valueOf(doc.getRole()))
                .addedBy(doc.getAddedBy())
                .addedAt(doc.getAddedAt())
                .build();
    }

    private ProjectMemberDocument toDocument(ProjectMember m) {
        return ProjectMemberDocument.builder()
                .id(m.getId())
                .projectId(m.getProjectId())
                .userId(m.getUserId())
                .role(m.getRole().name())
                .addedBy(m.getAddedBy())
                .addedAt(m.getAddedAt())
                .build();
    }
}
