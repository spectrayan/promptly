package com.promptly.project.infrastructure.persistence.r2dbc;

import com.promptly.AbstractR2dbcIntegrationTest;
import com.promptly.project.application.port.out.ProjectMemberPersistencePort;
import com.promptly.project.application.port.out.ProjectPersistencePort;
import com.promptly.project.domain.model.Project;
import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Project and ProjectMember R2DBC adapters.
 */
@SpringBootTest
class ProjectR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private ProjectPersistencePort projectPort;

    @Autowired
    private ProjectMemberPersistencePort memberPort;

    private Project buildProject(String name) {
        return Project.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .description("Test project: " + name)
                .tags(List.of("test", "r2dbc"))
                .createdBy("test-user")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldSaveAndFindProjectById() {
        var project = buildProject("R2DBC Project " + UUID.randomUUID());

        StepVerifier.create(
                projectPort.save(project)
                        .flatMap(saved -> projectPort.findById(saved.getId()))
        )
        .assertNext(found -> {
            assertThat(found.getName()).startsWith("R2DBC Project");
            assertThat(found.getDescription()).startsWith("Test project");
            assertThat(found.getCreatedBy()).isEqualTo("test-user");
        })
        .verifyComplete();
    }

    @Test
    void shouldCheckExistsByName() {
        var name = "Unique-" + UUID.randomUUID();
        var project = buildProject(name);

        StepVerifier.create(
                projectPort.save(project)
                        .then(projectPort.existsByName(name))
        )
        .assertNext(exists -> assertThat(exists).isTrue())
        .verifyComplete();
    }

    @Test
    void shouldSaveAndFindProjectMember() {
        var project = buildProject("Member Project " + UUID.randomUUID());

        StepVerifier.create(
                projectPort.save(project)
                        .flatMap(saved -> {
                            var member = ProjectMember.builder()
                                    .id(UUID.randomUUID().toString())
                                    .projectId(saved.getId())
                                    .userId("member-user")
                                    .role(ProjectRole.EDITOR)
                                    .addedBy("admin")
                                    .addedAt(Instant.now())
                                    .build();
                            return memberPort.save(member)
                                    .then(memberPort.findByProjectId(saved.getId()).collectList());
                        })
        )
        .assertNext(members -> {
            assertThat(members).hasSize(1);
            assertThat(members.get(0).getRole()).isEqualTo(ProjectRole.EDITOR);
            assertThat(members.get(0).getUserId()).isEqualTo("member-user");
        })
        .verifyComplete();
    }

    @Test
    void shouldFindProjectsByMemberUserId() {
        var project = buildProject("FindByMember " + UUID.randomUUID());
        var userId = "find-member-" + UUID.randomUUID();

        StepVerifier.create(
                projectPort.save(project)
                        .flatMap(saved -> {
                            var member = ProjectMember.builder()
                                    .id(UUID.randomUUID().toString())
                                    .projectId(saved.getId())
                                    .userId(userId)
                                    .role(ProjectRole.VIEWER)
                                    .addedBy("admin")
                                    .addedAt(Instant.now())
                                    .build();
                            return memberPort.save(member)
                                    .thenMany(projectPort.findByMemberUserId(userId))
                                    .collectList();
                        })
        )
        .assertNext(projects -> {
            assertThat(projects).hasSizeGreaterThanOrEqualTo(1);
        })
        .verifyComplete();
    }
}
