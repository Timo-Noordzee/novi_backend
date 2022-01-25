package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehiclePapersDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehiclePapersDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VehiclePapersMapper extends EntityMapper<VehiclePapersEntity, CreateVehiclePapersDto, UpdateVehiclePapersDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    VehiclePapersEntity fromCreateDto(final CreateVehiclePapersDto createDto);

    VehiclePapersEntity updateWithDto(final UpdateVehiclePapersDto updateDto, @MappingTarget final VehiclePapersEntity entity);

}
