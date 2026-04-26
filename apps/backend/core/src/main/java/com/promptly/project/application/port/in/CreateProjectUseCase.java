package com.promptly.project.application.port.in;

import com.promptly.project.domain.model.Project;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Inbound port for creating a new project.
 */
public interface CreateProjectUseCase {

    Mono<Project> createProject(CreateProjectCommand command);

    record CreateProjectCommand(String name, String description, List<String> tags, String createdBy) {}
}
