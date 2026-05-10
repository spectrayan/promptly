package com.spectrayan.promptly.prompt.application.service;


import com.spectrayan.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.spectrayan.promptly.prompt.application.port.out.PromptPersistencePort;
import com.spectrayan.promptly.project.application.port.out.ProjectPersistencePort;
import com.spectrayan.promptly.project.domain.model.Project;
import com.spectrayan.promptly.shared.systemprompt.SystemPromptPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Seeds the {@code __system__} project with default system prompts
 * on application startup if the project doesn't already exist.
 * <p>
 * This ensures that the Prompt Registry always contains a {@code __system__}
 * project with prompts for each AI feature (scanner, improver, etc.),
 * enabling admin customization from day one.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemProjectSeeder {

    private final ProjectPersistencePort projectRepository;
    private final PromptPersistencePort promptRepository;
    private final PromptHistoryPersistencePort historyRepository;
    private final SystemPromptPort systemPromptPort;

    /** Feature → prompt name mapping for seeding. */
    private static final Map<String, String> SYSTEM_PROMPTS = Map.of(
            "scanner", "scanner-system-prompt",
            "improver", "improver-system-prompt"
    );

    @EventListener(ApplicationReadyEvent.class)
    @Order(100) // Run after other initializers
    void seedSystemProject() {
        doSeed().subscribe(
                result -> log.info("System project seeding complete"),
                error -> log.error("Failed to seed __system__ project: {}", error.getMessage())
        );
    }

    /** Core seeding logic — package-private for testability. */
    reactor.core.publisher.Mono<Void> doSeed() {
        log.info("Checking for __system__ project...");

        return projectRepository.existsByName(SystemPromptPort.SYSTEM_PROJECT_ID)
                .flatMap(exists -> {
                    if (exists) {
                        log.info("__system__ project already exists, skipping seed");
                        return reactor.core.publisher.Mono.<Void>empty();
                    }
                    return createSystemProject();
                });
    }

    private reactor.core.publisher.Mono<Void> createSystemProject() {
        log.info("Creating __system__ project with default system prompts...");

        Project project = Project.builder()
                .name(SystemPromptPort.SYSTEM_PROJECT_ID)
                .description("System-managed project containing AI feature prompts. " +
                             "Administrators can edit these prompts to customize scanner, improver, and other AI behaviors.")
                .tags(List.of("system", "internal"))
                .createdBy("system")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return projectRepository.save(project)
                .doOnNext(saved -> log.info("__system__ project created: id={}", saved.getId()))
                .flatMap(saved -> seedDefaultPrompts(saved.getId()))
                .then();
    }

    private reactor.core.publisher.Mono<Void> seedDefaultPrompts(String projectId) {
        return reactor.core.publisher.Flux.fromIterable(SYSTEM_PROMPTS.entrySet())
                .flatMap(entry -> seedPrompt(projectId, entry.getKey(), entry.getValue()))
                .then();
    }

    private reactor.core.publisher.Mono<com.spectrayan.promptly.prompt.domain.model.Prompt> seedPrompt(
            String projectId, String feature, String promptName) {

        String defaultContent = systemPromptPort.getDefaultPrompt(feature);
        if (defaultContent == null || defaultContent.isBlank()) {
            log.warn("No default prompt found for feature '{}', skipping", feature);
            return reactor.core.publisher.Mono.empty();
        }

        // Start as DRAFT so createNewVersion's business rule passes,
        // then approve after the initial version is created.
        var prompt = com.spectrayan.promptly.prompt.domain.model.Prompt.builder()
                .name(promptName)
                .description("System prompt for the " + feature + " feature. " +
                             "Edit this prompt to customize AI behavior.")
                .projectId(projectId)
                .contentFormat(com.spectrayan.promptly.prompt.domain.model.ContentFormat.MARKDOWN)
                .tags(new java.util.HashSet<>(java.util.Set.of("system", feature)))
                .currentVersion(0)
                .status(com.spectrayan.promptly.prompt.domain.model.PromptStatus.DRAFT)
                .build();

        prompt.createNewVersion(defaultContent, "Initial system default", "system");
        prompt.markApproved();

        var initialVersion = prompt.getVersions().get(0);
        return promptRepository.save(prompt)
                .flatMap(saved -> historyRepository.save(saved.getId(), initialVersion)
                        .thenReturn(saved))
                .doOnNext(saved -> log.info("Seeded system prompt '{}' (id={}) for feature '{}'",
                        promptName, saved.getId(), feature));
    }
}
