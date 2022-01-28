package com.timo_noordzee.novi.backend.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import com.timo_noordzee.novi.backend.exception.UnknownRepairLineTypeException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum RepairLineType {
    PART(0),
    ACTION(1);

    private final int value;

    public static RepairLineType parse(final int value) {
        return Arrays.stream(values())
                .filter(role -> role.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new UnknownRepairLineTypeException(value));
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
