package com.timo_noordzee.novi.backend.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import com.timo_noordzee.novi.backend.exception.UnknownRoleException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("admin"),
    ADMINISTRATIVE("administrative"),
    BACKOFFICE("backoffice"),
    CASHIER("cashier"),
    MECHANIC("mechanic");

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ADMINISTRATIVE = "ADMINISTRATIVE";
    public static final String ROLE_BACKOFFICE = "BACKOFFICE";
    public static final String ROLE_CASHIER = "CASHIER";
    public static final String ROLE_MECHANIC = "MECHANIC";

    private final String value;

    public static Role parse(final String value) {
        return Arrays.stream(values())
                .filter(role -> StringUtils.equalsIgnoreCase(role.getValue(), value))
                .findFirst()
                .orElseThrow(() -> new UnknownRoleException(value));
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
