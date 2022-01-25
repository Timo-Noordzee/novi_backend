package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import net.datafaker.Faker;

import java.util.Locale;
import java.util.UUID;

public class ActionTestUtils {

    private final Faker faker;

    public ActionTestUtils() {
        this.faker = new Faker(new Locale("nl"));
    }

    public ActionEntity generateMockEntity() {
        return ActionEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.lorem().word())
                .price(faker.number().randomDouble(2, 0, 9999))
                .build();
    }

    public CreateActionDto generateMockCreateDto() {
        return CreateActionDto.builder()
                .name(faker.lorem().word())
                .price(faker.number().randomDouble(2, 0, 9999))
                .build();
    }

    public UpdateActionDto generateMockUpdateDto() {
        return UpdateActionDto.builder()
                .name(faker.lorem().word())
                .price(faker.number().randomDouble(2, 0, 9999))
                .build();
    }

}
