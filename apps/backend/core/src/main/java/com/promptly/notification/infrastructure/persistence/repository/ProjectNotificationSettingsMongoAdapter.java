package com.promptly.notification.infrastructure.persistence.repository;

import com.promptly.notification.application.port.out.ProjectNotificationSettingsRepository;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import com.promptly.notification.infrastructure.persistence.entity.ProjectNotificationSettingsDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectNotificationSettingsMongoAdapter implements ProjectNotificationSettingsRepository {

    private final ProjectNotificationSettingsReactiveMongoRepository mongoRepo;

    @Override
    public Mono<ProjectNotificationSettings> findByProjectId(String projectId) {
        return mongoRepo.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Mono<ProjectNotificationSettings> save(ProjectNotificationSettings settings) {
        ProjectNotificationSettingsDocument doc = toDocument(settings);
        if (doc.getId() == null) {
            doc.setId(UUID.randomUUID().toString());
        }
        doc.setUpdatedAt(Instant.now());
        return mongoRepo.save(doc).map(this::toDomain);
    }

    private ProjectNotificationSettingsDocument toDocument(ProjectNotificationSettings s) {
        return ProjectNotificationSettingsDocument.builder()
                .id(s.getId())
                .projectId(s.getProjectId())
                .enabledEvents(s.getEnabledEvents())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private ProjectNotificationSettings toDomain(ProjectNotificationSettingsDocument doc) {
        return ProjectNotificationSettings.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .enabledEvents(doc.getEnabledEvents())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
