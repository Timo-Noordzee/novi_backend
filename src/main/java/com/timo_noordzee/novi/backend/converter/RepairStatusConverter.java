package com.timo_noordzee.novi.backend.converter;

import com.timo_noordzee.novi.backend.domain.RepairStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RepairStatusConverter implements AttributeConverter<RepairStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final RepairStatus repairStatus) {
        return repairStatus.getValue();
    }

    @Override
    public RepairStatus convertToEntityAttribute(final Integer value) {
        return RepairStatus.parse(value);
    }
    
}
