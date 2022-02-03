package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import com.timo_noordzee.novi.backend.domain.Role;
import com.timo_noordzee.novi.backend.dto.CreateEmployeeDto;
import com.timo_noordzee.novi.backend.dto.UpdateEmployeeDto;
import net.datafaker.Faker;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class EmployeeTestUtils {

    private final Faker faker;

    public EmployeeTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public Role randomRole() {
        final int roleIndex = new Random().nextInt(Role.values().length);
        return Role.values()[roleIndex];
    }

    public String randomPassword() {
        return faker.internet().password();
    }

    public EmployeeEntity generateMockEntity() {
        return EmployeeEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.regexify("^\\$2[ayb]\\$[a-zA-Z0-9]{56}$"))
                .role(randomRole())
                .build();
    }

    public CreateEmployeeDto generateMockCreateDto() {
        return generateMockCreateDto(randomRole());
    }

    public CreateEmployeeDto generateMockCreateDto(final Role role) {
        return CreateEmployeeDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .role(role.getValue())
                .build();
    }

    public UpdateEmployeeDto generateMockUpdateDto(){
        return generateMockUpdateDto(randomRole(), randomPassword());
    }

    public UpdateEmployeeDto generateMockUpdateDto(final Role role) {
        return generateMockUpdateDto(role, randomPassword());
    }

    public UpdateEmployeeDto generateMockUpdateDto(final Role role, final String password) {
        return UpdateEmployeeDto.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .role(role.getValue())
                .password(password)
                .build();
    }

}
