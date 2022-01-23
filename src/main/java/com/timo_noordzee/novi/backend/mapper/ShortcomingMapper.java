package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ShortcomingMapper extends EntityMapper<ShortcomingEntity, CreateShortcomingDto, UpdateShortcomingDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    ShortcomingEntity fromCreateDto(final CreateShortcomingDto createDto);

    ShortcomingEntity updateWithDto(final UpdateShortcomingDto updateDto, @MappingTarget final ShortcomingEntity entity);

}
