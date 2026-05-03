package com.promptly.search.infrastructure.persistence.r2dbc.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code prompt_embeddings} table.
 * <p>
 * The {@code embedding} column uses pgvector's {@code vector(768)} type.
 * Since Spring Data R2DBC doesn't have native pgvector support,
 * we handle embedding as a raw float array and use custom SQL in the adapter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("prompt_embeddings")
public class PromptEmbeddingR2dbcEntity {

    @Id
    private String id;

    @Column("prompt_id")
    private String promptId;

    @Column("project_id")
    private String projectId;

    @Column("content_hash")
    private String contentHash;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    // Note: 'embedding' column is handled via custom @Query in the adapter
    // because R2DBC doesn't natively support pgvector types.
}
