package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.dto.UpdateCustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerMapper extends EntityMapper<CustomerEntity, CreateCustomerDto, UpdateCustomerDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    CustomerEntity fromCreateDto(final CreateCustomerDto createDto);

    CustomerEntity updateWithDto(final UpdateCustomerDto updateDto, @MappingTarget final CustomerEntity entity);

}
