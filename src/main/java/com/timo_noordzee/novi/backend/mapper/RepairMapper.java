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
    RepairEntity fromCreateDto(final CreateRepairDto createDto);

    RepairEntity updateWithDto(final UpdateRepairDto updateDto, @MappingTarget final RepairEntity entity);

    default RepairStatus fromInteger(final Integer status) {
        return RepairStatus.parse(status);
    }

    default RepairStatus fromInteger(final int status) {
        return RepairStatus.parse(status);
    }

}
