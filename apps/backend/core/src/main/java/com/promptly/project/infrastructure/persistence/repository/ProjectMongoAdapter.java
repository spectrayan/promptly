package com.promptly.project.infrastructure.persistence.repository;

import com.promptly.project.application.port.out.ProjectRepository;
import com.promptly.project.domain.model.Project;
import com.promptly.project.infrastructure.persistence.entity.ProjectDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ProjectMongoAdapter implements ProjectRepository {

    private final ProjectReactiveMongoRepository mongoRepo;
    private final ProjectMemberReactiveMongoRepository memberRepo;

    @Override
    public Mono<Project> save(Project project) {
        return mongoRepo.save(toDocument(project)).map(this::toDomain);
    }

    @Override
    public Mono<Project> findById(String id) {
        return mongoRepo.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Project> findByMemberUserId(String userId) {
        return memberRepo.findByUserId(userId)
                .flatMap(member -> mongoRepo.findById(member.getProjectId()))
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mongoRepo.existsByName(name);
    }

    private Project toDomain(ProjectDocument doc) {
        return Project.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .tags(doc.getTags() != null ? doc.getTags() : new ArrayList<>())
                .createdBy(doc.getCreatedBy())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    private ProjectDocument toDocument(Project p) {
        return ProjectDocument.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .tags(p.getTags())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
