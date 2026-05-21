package com.spectrayan.promptly.audit.infrastructure.persistence.mongo.mapper;

import com.spectrayan.promptly.audit.domain.model.AuditEntry;
import com.spectrayan.promptly.audit.infrastructure.persistence.mongo.entity.AuditDocument;
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
