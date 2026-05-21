package com.spectrayan.promptly.workflow.infrastructure.persistence.mongo.mapper;

import com.spectrayan.promptly.workflow.domain.model.*;
import com.spectrayan.promptly.workflow.infrastructure.persistence.mongo.entity.WorkflowDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper between Workflow domain models and MongoDB documents.
 * <p>
 * Steps are stored in a separate collection and are not mapped here.
 * The {@code steps} field on the domain model is ignored during mapping;
 * it is populated by the application service from {@code WorkflowStepPersistencePort}.
 */
@Mapper(componentModel = "spring")
public interface WorkflowPersistenceMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    WorkflowDocument toDocument(Workflow workflow);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "steps", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Workflow toDomain(WorkflowDocument document);

    @Named("statusToString")
    default String statusToString(WorkflowStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default WorkflowStatus stringToStatus(String status) {
        return status != null ? WorkflowStatus.valueOf(status) : null;
    }

}
