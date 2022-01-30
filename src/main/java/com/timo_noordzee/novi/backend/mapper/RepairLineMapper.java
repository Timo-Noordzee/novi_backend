package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.RepairLineEntity;
import com.timo_noordzee.novi.backend.domain.RepairLineType;
import com.timo_noordzee.novi.backend.dto.AddRepairLinesDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairLineDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {UUID.class, RepairLineType.class}
)
public interface RepairLineMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "type", expression = "java(RepairLineType.PART)")
    RepairLineEntity fromPartEntity(final PartEntity partEntity, final RepairEntity repair, final int amount);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "type", expression = "java(RepairLineType.ACTION)")
    RepairLineEntity fromActionEntity(final ActionEntity actionEntity, final RepairEntity repair, final int amount);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    RepairLineEntity fromCustomDto(final AddRepairLinesDto.Custom customDto, final RepairEntity repair);

    RepairLineEntity updateWithDto(final UpdateRepairLineDto updateDto, @MappingTarget final RepairLineEntity entity);

    default RepairLineType fromInteger(final Integer status) {
        return RepairLineType.parse(status);
    }

}
