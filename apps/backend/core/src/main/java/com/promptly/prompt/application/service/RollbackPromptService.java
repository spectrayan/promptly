package com.promptly.prompt.application.service;

import com.promptly.shared.domain.event.PromptRolledBack;
import com.promptly.prompt.application.port.in.RollbackPromptUseCase;
import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptVersion;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Application service for rolling back a prompt to a previous version.
 * Creates a new version with the content from the target version.
 * Business rule enforcement (isRollbackable) is delegated to the Prompt aggregate.
 * <p>
 * Fetches the target version from {@link PromptHistoryPersistencePort} (not from the
 * prompt document) and persists the rollback version as a new history entry.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RollbackPromptService implements RollbackPromptUseCase {

    private final PromptPersistencePort promptRepository;
    private final PromptHistoryPersistencePort historyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Prompt> rollbackToVersion(RollbackPromptCommand command) {
        log.info("Rolling back prompt {} to version {}", command.promptId(), command.targetVersion());
        return promptRepository.findById(command.promptId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", command.promptId())))
                .flatMap(prompt -> {
                    // Business rule check
                    prompt.assertRollbackable();

                    int fromVersion = prompt.getCurrentVersion();

                    // Fetch target version content from history
                    return historyRepository.findByPromptIdAndVersion(command.promptId(), command.targetVersion())
                            .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                                    "PromptVersion", command.promptId() + "/v" + command.targetVersion())))
                            .flatMap(targetVersion -> {
                                // createNewVersion checks isEditable, which is satisfied for DRAFT status
                                PromptVersion rollbackVersion = prompt.createNewVersion(
                                        targetVersion.getContent(),
                                        "Rollback to version " + command.targetVersion(),
                                        command.author()
                                );
                                return promptRepository.save(prompt)
                                        .flatMap(saved -> historyRepository.save(saved.getId(), rollbackVersion)
                                                .thenReturn(saved))
                                        .doOnSuccess(saved -> {
                                            log.info("Prompt rolled back: id={}, from={}, to={}",
                                                    command.promptId(), fromVersion, command.targetVersion());
                                            eventPublisher.publishEvent(
                                                    new PromptRolledBack(saved.getId(), fromVersion, command.targetVersion())
                                            );
                                        });
                            });
                });
    }
}
