package com.spectrayan.promptly.prompt.infrastructure.persistence.r2dbc.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code prompt_history} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("prompt_history")
public class PromptHistoryR2dbcEntity {

    @Id
    private String id;

    @Column("prompt_id")
    private String promptId;

    @Column("version_number")
    private int versionNumber;

    private String content;

    @Column("change_message")
    private String changeMessage;

    @Column("created_by")
    private String createdBy;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;
}
