package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.dto.CreatePartDto;
import com.timo_noordzee.novi.backend.dto.UpdatePartDto;
import net.datafaker.Faker;

import java.util.Locale;
import java.util.UUID;

public class PartTestUtils {

    public final Faker faker;

    public PartTestUtils() {
        this.faker = new Faker(new Locale("nl"));
    }

    public PartEntity generateMockEntity() {
        return generateMockEntity(faker.number().numberBetween(0, 99));
    }

    public PartEntity generateMockEntity(final int stock) {
        return PartEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.vehicle().carOptions(1, 1).get(0))
                .price(faker.number().randomDouble(2, 0, 9999))
                .stock(stock)
                .build();
    }

    public CreatePartDto generateMockCreateDto() {
        return CreatePartDto.builder()
                .name(faker.vehicle().carOptions(1, 1).get(0))
                .price(faker.number().randomDouble(2, 0, 9999))
                .stock(faker.number().numberBetween(0, 99))
                .build();
    }

    public UpdatePartDto generateMockUpdateDto() {
        return UpdatePartDto.builder()
                .name(faker.vehicle().carOptions(1, 1).get(0))
                .price(faker.number().randomDouble(2, 0, 9999))
                .stock(faker.number().numberBetween(0, 99))
                .build();
    }

}
