package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.dto.UpdateCustomerDto;
import net.datafaker.Faker;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CustomerTestUtils {

    private final Faker faker;

    public CustomerTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public CustomerEntity generateMockEntity() {
        return CustomerEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .createdAt(faker.date().past(10, TimeUnit.DAYS))
                .build();
    }

    public CreateCustomerDto generateMockCreateDto() {
        return CreateCustomerDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();
    }

    public UpdateCustomerDto generateMockUpdateDto() {
        return UpdateCustomerDto.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();
    }

}
