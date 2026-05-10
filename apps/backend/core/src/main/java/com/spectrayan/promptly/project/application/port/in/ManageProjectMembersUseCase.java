package com.spectrayan.promptly.project.application.port.in;

import com.spectrayan.promptly.project.domain.model.ProjectMember;
import com.spectrayan.promptly.project.domain.model.ProjectRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port for managing project membership (add, update, remove, list, query role).
 */
public interface ManageProjectMembersUseCase {

    Mono<ProjectMember> addMember(AddMemberCommand command);

    Flux<ProjectMember> listMembers(String projectId);

    Mono<ProjectRole> getUserRole(String projectId, String userId);

    Mono<ProjectMember> updateMember(UpdateMemberCommand command);

    Mono<Void> removeMember(String projectId, String userId);

    record AddMemberCommand(String projectId, String userId, ProjectRole role, String addedBy) {}

    record UpdateMemberCommand(String projectId, String userId, ProjectRole role) {}
}
