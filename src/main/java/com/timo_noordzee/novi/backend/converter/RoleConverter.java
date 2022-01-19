package com.timo_noordzee.novi.backend.converter;

import com.timo_noordzee.novi.backend.domain.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(final Role role) {
        return role.getValue();
    }

    @Override
    public Role convertToEntityAttribute(final String value) {
        return Role.parse(value);
    }

}
