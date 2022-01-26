package com.timo_noordzee.novi.backend.converter;

import com.timo_noordzee.novi.backend.exception.UnknownStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepairStatusConverterTest {

    private RepairStatusConverter repairStatusConverter;

    @BeforeEach
    void setup() {
        repairStatusConverter = new RepairStatusConverter();
    }

    @Test
    void convertingUnknownRoleThrowsUnknownRoleException() {
        final int value = -1;

        assertThrows(UnknownStatusException.class, () -> repairStatusConverter.convertToEntityAttribute(value));
    }

}
