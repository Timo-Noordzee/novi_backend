package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VehicleMapper extends EntityMapper<VehicleEntity, CreateVehicleDto, UpdateVehicleDto> {

    VehicleEntity fromCreateDto(final CreateVehicleDto createDto);

    VehicleEntity updateWithDto(final UpdateVehicleDto updateDto, @MappingTarget final VehicleEntity entity);

}