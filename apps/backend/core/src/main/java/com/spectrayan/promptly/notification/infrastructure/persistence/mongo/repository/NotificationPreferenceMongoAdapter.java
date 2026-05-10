package com.spectrayan.promptly.notification.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.notification.application.port.out.NotificationPreferencePersistencePort;
import com.spectrayan.promptly.notification.domain.model.NotificationPreference;
import com.spectrayan.promptly.notification.infrastructure.persistence.mongo.entity.NotificationPreferenceDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class NotificationPreferenceMongoAdapter implements NotificationPreferencePersistencePort {

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
