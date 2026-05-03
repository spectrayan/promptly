package com.promptly.scanner.infrastructure.persistence.mongo.mapper;

import com.promptly.scanner.domain.model.Finding;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.scanner.domain.model.Severity;
import com.promptly.scanner.infrastructure.persistence.mongo.entity.ScanResultDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for ScanResult domain/document conversion.
 */
@Mapper(componentModel = "spring")
public interface ScanResultPersistenceMapper {

    ScanResultDocument toDocument(ScanResult scanResult);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ScanResult toDomain(ScanResultDocument document);

    @Mapping(target = "severity", source = "severity", qualifiedByName = "severityToString")
    ScanResultDocument.FindingSubdocument toFindingDoc(Finding finding);

    @Mapping(target = "severity", source = "severity", qualifiedByName = "stringToSeverity")
    Finding toFindingDomain(ScanResultDocument.FindingSubdocument subdoc);

    @Named("severityToString")
    default String severityToString(Severity severity) {
        return severity != null ? severity.name() : null;
    }

    @Named("stringToSeverity")
    default Severity stringToSeverity(String severity) {
        return severity != null ? Severity.valueOf(severity.toUpperCase()) : null;
    }

}
