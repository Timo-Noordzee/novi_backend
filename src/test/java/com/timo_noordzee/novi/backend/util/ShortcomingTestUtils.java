package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import net.datafaker.Faker;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ShortcomingTestUtils {

    private final Faker faker;

    public ShortcomingTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public ShortcomingEntity generateMockEntity() {
        return generateMockEntity(null);
    }

    public ShortcomingEntity generateMockEntity(final VehicleEntity vehicleEntity) {
        return ShortcomingEntity.builder()
                .id(UUID.randomUUID())
                .description(faker.vehicle().carOptions(1, 1).get(0))
                .vehicle(vehicleEntity)
                .createdAt(faker.date().past(10, TimeUnit.DAYS))
                .build();
    }

    public CreateShortcomingDto generateMockCreateDto(final String vehicleId) {
        return CreateShortcomingDto.builder()
                .description(faker.vehicle().carOptions(1, 1).get(0))
                .vehicleId(vehicleId)
                .build();
    }

    public UpdateShortcomingDto generateMockUpdateDto() {
        final UpdateShortcomingDto updateShortcomingDto = new UpdateShortcomingDto();
        updateShortcomingDto.setDescription(faker.vehicle().carOptions(1, 1).get(0));
        return updateShortcomingDto;
    }

}
