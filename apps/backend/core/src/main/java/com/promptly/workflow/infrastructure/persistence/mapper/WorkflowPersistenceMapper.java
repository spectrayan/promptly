package com.promptly.workflow.infrastructure.persistence.mapper;

import com.promptly.workflow.domain.model.*;
import com.promptly.workflow.infrastructure.persistence.entity.WorkflowDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper between Workflow domain models and MongoDB documents.
 */
@Mapper(componentModel = "spring")
public interface WorkflowPersistenceMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "sourceEnvironment", source = "sourceEnvironment", qualifiedByName = "envToString")
    @Mapping(target = "targetEnvironment", source = "targetEnvironment", qualifiedByName = "envToString")
    WorkflowDocument toDocument(Workflow workflow);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "sourceEnvironment", source = "sourceEnvironment", qualifiedByName = "stringToEnv")
    @Mapping(target = "targetEnvironment", source = "targetEnvironment", qualifiedByName = "stringToEnv")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Workflow toDomain(WorkflowDocument document);

    WorkflowDocument.WorkflowStepSubdocument toStepDoc(WorkflowStep step);
    WorkflowStep toStepDomain(WorkflowDocument.WorkflowStepSubdocument subdoc);

    @Named("statusToString")
    default String statusToString(WorkflowStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default WorkflowStatus stringToStatus(String status) {
        return status != null ? WorkflowStatus.valueOf(status) : null;
    }

    @Named("envToString")
    default String envToString(Environment env) {
        return env != null ? env.name() : null;
    }

    @Named("stringToEnv")
    default Environment stringToEnv(String env) {
        return env != null ? Environment.valueOf(env) : null;
    }

}
