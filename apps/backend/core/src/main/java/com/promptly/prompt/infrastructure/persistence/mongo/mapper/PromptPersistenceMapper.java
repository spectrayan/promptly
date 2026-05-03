package com.promptly.prompt.infrastructure.persistence.mongo.mapper;

import com.promptly.prompt.domain.model.ContentFormat;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptMetadata;
import com.promptly.prompt.domain.model.PromptStatus;
import com.promptly.prompt.infrastructure.persistence.mongo.entity.PromptDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper between domain models and MongoDB documents.
 * Converts Prompt ↔ PromptDocument at the persistence boundary.
 */
@Mapper(componentModel = "spring")
public interface PromptPersistenceMapper {

    @Mapping(target = "contentFormat", source = "contentFormat", qualifiedByName = "formatToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "id", source = "id")
    PromptDocument toDocument(Prompt prompt);

    @Mapping(target = "contentFormat", source = "contentFormat", qualifiedByName = "stringToFormat")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versions", ignore = true)
    Prompt toDomain(PromptDocument document);

    PromptDocument.MetadataSubdocument toMetadataDoc(PromptMetadata metadata);

    PromptMetadata toMetadataDomain(PromptDocument.MetadataSubdocument subdocument);

    @Named("formatToString")
    default String formatToString(ContentFormat format) {
        return format != null ? format.name() : null;
    }

    @Named("stringToFormat")
    default ContentFormat stringToFormat(String format) {
        return format != null ? ContentFormat.valueOf(format) : null;
    }

    @Named("statusToString")
    default String statusToString(PromptStatus status) {
        return status != null ? status.name() : PromptStatus.DRAFT.name();
    }

    @Named("stringToStatus")
    default PromptStatus stringToStatus(String status) {
        if (status == null) return PromptStatus.DRAFT;
        try {
            return PromptStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return PromptStatus.DRAFT;
        }
    }

}
