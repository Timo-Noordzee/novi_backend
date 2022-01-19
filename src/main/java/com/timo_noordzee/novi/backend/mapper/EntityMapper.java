package com.timo_noordzee.novi.backend.mapper;

import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.UUID;

public interface EntityMapper<E, C, U> {

    E fromCreateDto(final C createDto);

    E updateWithDto(final U updateDto, @MappingTarget final E entity);

    @Named("parseUUIDOrRandom")
    default UUID parseUUIDOrRandom(final String id) {
        try {
            return UUID.fromString(id);
        } catch (final Exception ignore) {
            return UUID.randomUUID();
        }
    }

}
