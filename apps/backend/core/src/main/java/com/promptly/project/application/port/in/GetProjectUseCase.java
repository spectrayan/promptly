package com.promptly.project.application.port.in;

import com.promptly.project.domain.model.Project;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port for querying projects.
 */
public interface GetProjectUseCase {

    Mono<Project> getProject(String id);

    Flux<Project> listProjects(String userId);
}
