package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.domain.RepairStatus;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RepairMapper extends EntityMapper<RepairEntity, CreateRepairDto, UpdateRepairDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    @Mapping(source = "status", target = "status", qualifiedByName = "parseRepairStatus")
    RepairEntity fromCreateDto(final CreateRepairDto createDto);

    @Mapping(source = "status", target = "status", qualifiedByName = "parseRepairStatus")
    RepairEntity updateWithDto(final UpdateRepairDto updateDto, @MappingTarget final RepairEntity entity);

    @Named("parseRepairStatus")
    default RepairStatus fromInteger(final Integer status) {
        return RepairStatus.parse(status);
    }

}
