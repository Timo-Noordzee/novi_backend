package com.timo_noordzee.novi.backend.converter;

import com.timo_noordzee.novi.backend.exception.UnknownRoleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoleConverterTest {

    private RoleConverter roleConverter;

    @BeforeEach
    void setup() {
        roleConverter = new RoleConverter();
    }

    @Test
    void convertingUnknownRoleThrowsUnknownRoleException() {
        final UnknownRoleException exception = assertThrows(UnknownRoleException.class, () ->
                roleConverter.convertToEntityAttribute("unknown-role"));

        assertThat(exception.getErrorCode()).isEqualTo(UnknownRoleException.ERROR_CODE);
    }
}
