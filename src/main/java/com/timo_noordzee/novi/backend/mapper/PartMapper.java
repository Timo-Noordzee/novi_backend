package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.dto.CreatePartDto;
import com.timo_noordzee.novi.backend.dto.UpdatePartDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PartMapper extends EntityMapper<PartEntity, CreatePartDto, UpdatePartDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    PartEntity fromCreateDto(final CreatePartDto createDto);

    PartEntity updateWithDto(final UpdatePartDto updateDto, @MappingTarget final PartEntity entity);

    default double fromDouble(final Double value){
        return value;
    }

}
