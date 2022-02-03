package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ActionMapper extends EntityMapper<ActionEntity, CreateActionDto, UpdateActionDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    ActionEntity fromCreateDto(final CreateActionDto createDto);

    ActionEntity updateWithDto(final UpdateActionDto updateDto, @MappingTarget final ActionEntity entity);

}
