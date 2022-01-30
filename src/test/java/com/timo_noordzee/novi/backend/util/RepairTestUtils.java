package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.domain.RepairStatus;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RepairTestUtils {

    private final Faker faker;

    public RepairTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public RepairStatus randomRepairStatus() {
        final int index = new Random().nextInt(RepairStatus.values().length);
        return RepairStatus.values()[index];
    }

    public RepairEntity generateMockEntity() {
        return generateMockEntity(null);
    }

    public RepairEntity generateMockEntity(final VehicleEntity vehicleEntity) {
        return RepairEntity.builder()
                .id(UUID.randomUUID())
                .remarks(faker.lorem().paragraph())
                .status(randomRepairStatus())
                .createdAt(faker.date().past(10, TimeUnit.DAYS))
                .vehicle(vehicleEntity)
                .lines(new ArrayList<>())
                .build();
    }

    public CreateRepairDto generateMockCreateDto(final String vehicleId) {
        return CreateRepairDto.builder()
                .remarks(faker.lorem().paragraph())
                .status(randomRepairStatus().getValue())
                .vehicleId(vehicleId)
                .build();
    }

    public UpdateRepairDto generateMockUpdateDto() {
        return UpdateRepairDto.builder()
                .remarks(faker.lorem().paragraph())
                .status(randomRepairStatus().getValue())
                .build();
    }

}
