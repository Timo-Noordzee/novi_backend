package com.timo_noordzee.novi.backend.mapper;

import com.timo_noordzee.novi.backend.data.InvoiceEntity;
import com.timo_noordzee.novi.backend.domain.InvoiceStatus;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface InvoiceMapper extends EntityMapper<InvoiceEntity, CreateInvoiceDto, UpdateInvoiceDto> {

    @Mapping(source = "id", target = "id", qualifiedByName = "parseUUIDOrRandom")
    InvoiceEntity fromCreateDto(final CreateInvoiceDto createDto);

    InvoiceEntity updateWithDto(final UpdateInvoiceDto updateDto, @MappingTarget final InvoiceEntity entity);

    default InvoiceStatus fromInteger(final int status) {
        return InvoiceStatus.parse(status);
    }

}
