package com.promptly.scanner.infrastructure.persistence.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code scan_results} table.
 * <p>
 * Findings are stored as a JSON string column (TEXT/JSONB depending on dialect).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("scan_results")
public class ScanResultR2dbcEntity {

    @Id
    private String id;

    @Column("project_id")
    private String projectId;

    @Column("prompt_id")
    private String promptId;

    @Column("prompt_version")
    private int promptVersion;

    @Column("overall_score")
    private double overallScore;

    private String status;

    @Column("llm_provider")
    private String llmProvider;

    @Column("llm_model")
    private String llmModel;

    @Column("scanned_by")
    private String scannedBy;

    /** JSON array of findings — stored as TEXT/JSONB depending on the SQL dialect. */
    private String findings;

    @Column("scanned_at")
    private Instant scannedAt;

    @Version
    private Long version;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
