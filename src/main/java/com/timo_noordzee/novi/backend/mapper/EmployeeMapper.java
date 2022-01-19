package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import com.timo_noordzee.novi.backend.domain.Role;
import com.timo_noordzee.novi.backend.dto.CreateEmployeeDto;
import com.timo_noordzee.novi.backend.dto.UpdateEmployeeDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EmployeeMapper extends EntityMapper<EmployeeEntity, CreateEmployeeDto, UpdateEmployeeDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    @Mapping(source = "role", target = "role", qualifiedByName = "convertStringToRole")
    EmployeeEntity fromCreateDto(final CreateEmployeeDto createDto);

    @Mapping(source = "role", target = "role", qualifiedByName = "convertStringToRole")
    EmployeeEntity updateWithDto(final UpdateEmployeeDto updateDto, @MappingTarget final EmployeeEntity entity);

    @Named("convertStringToRole")
    default Role convertStringToRole(final String value) {
        return Role.parse(value);
    }

}
