package com.promptly.audit.infrastructure.persistence.mapper;

import com.promptly.audit.domain.model.AuditEntry;
import com.promptly.audit.infrastructure.persistence.entity.AuditDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for AuditEntry domain/document conversion.
 */
@Mapper(componentModel = "spring")
public interface AuditPersistenceMapper {

    AuditDocument toDocument(AuditEntry entry);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AuditEntry toDomain(AuditDocument document);

}
