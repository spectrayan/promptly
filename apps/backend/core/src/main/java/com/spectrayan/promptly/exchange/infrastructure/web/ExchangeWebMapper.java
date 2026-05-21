package com.spectrayan.promptly.exchange.infrastructure.web;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExchangeWebMapper {

    com.spectrayan.promptly.infrastructure.in.web.dto.ExportManifest toDto(com.spectrayan.promptly.exchange.domain.model.ExportManifest domain);

    com.spectrayan.promptly.exchange.domain.model.ExportManifest toDomain(com.spectrayan.promptly.infrastructure.in.web.dto.ExportManifest dto);

    default OffsetDateTime map(Instant value) {
        return value != null ? value.atOffset(ZoneOffset.UTC) : null;
    }

    default Instant map(OffsetDateTime value) {
        return value != null ? value.toInstant() : null;
    }
}
