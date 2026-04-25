package com.promptly.notification.infrastructure.persistence.repository;

import com.promptly.notification.application.port.out.NotificationPreferenceRepository;
import com.promptly.notification.domain.model.NotificationPreference;
import com.promptly.notification.infrastructure.persistence.entity.NotificationPreferenceDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPreferenceMongoAdapter implements NotificationPreferenceRepository {

    private final NotificationPreferenceReactiveMongoRepository mongoRepo;

    @Override
    public Mono<NotificationPreference> findByUserIdAndProjectId(String userId, String projectId) {
        return mongoRepo.findByUserIdAndProjectId(userId, projectId).map(this::toDomain);
    }

    @Override
    public Mono<NotificationPreference> save(NotificationPreference pref) {
        NotificationPreferenceDocument doc = toDocument(pref);
        if (doc.getId() == null) {
            doc.setId(UUID.randomUUID().toString());
        }
        doc.setUpdatedAt(Instant.now());
        return mongoRepo.save(doc).map(this::toDomain);
    }

    private NotificationPreferenceDocument toDocument(NotificationPreference p) {
        return NotificationPreferenceDocument.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .projectId(p.getProjectId())
                .mutedEvents(p.getMutedEvents())
                .inAppEnabled(p.isInAppEnabled())
                .emailEnabled(p.isEmailEnabled())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private NotificationPreference toDomain(NotificationPreferenceDocument doc) {
        return NotificationPreference.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .projectId(doc.getProjectId())
                .mutedEvents(doc.getMutedEvents())
                .inAppEnabled(doc.isInAppEnabled())
                .emailEnabled(doc.isEmailEnabled())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
