package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VehicleTestUtils {

    private final Faker faker;

    public VehicleTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public String randomVin() {
        return faker.vehicle().vin();
    }

    public String randomCustomerId() {
        return UUID.randomUUID().toString();
    }

    public VehicleEntity generateMockEntity() {
        return generateMockEntity(null);
    }

    public VehicleEntity generateMockEntity(final CustomerEntity customerEntity) {
        return VehicleEntity.builder()
                .vin(faker.vehicle().vin())
                .brand(faker.vehicle().manufacturer())
                .make(faker.vehicle().make())
                .license(faker.vehicle().licensePlate())
                .createdAt(faker.date().past(10, TimeUnit.DAYS))
                .owner(customerEntity)
                .build();
    }

    public CreateVehicleDto generateMockCreateDto(final UUID id) {
        return generateMockCreateDto(id.toString());
    }

    public CreateVehicleDto generateMockCreateDto(final String customerId) {
        return CreateVehicleDto.builder()
                .vin(faker.vehicle().vin())
                .license(faker.vehicle().licensePlate())
                .brand(faker.vehicle().manufacturer())
                .make(faker.vehicle().make())
                .year(faker.number().numberBetween(1900, 2022))
                .customerId(customerId)
                .build();
    }

    public UpdateVehicleDto generateMockUpdateDto() {
        return generateMockUpdateDto(null);
    }

    public UpdateVehicleDto generateMockUpdateDto(final String customerId) {
        final UpdateVehicleDto.UpdateVehicleDtoBuilder builder = UpdateVehicleDto.builder()
                .license(faker.vehicle().licensePlate())
                .brand(faker.vehicle().manufacturer())
                .make(faker.vehicle().make())
                .year(faker.number().numberBetween(1900, 2022));

        if (StringUtils.isNotEmpty(customerId)) {
            builder.customerId(customerId);
        }

        return builder.build();
    }

}
