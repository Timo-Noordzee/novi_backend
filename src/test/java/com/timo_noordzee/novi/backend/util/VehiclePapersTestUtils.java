package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehiclePapersDto;
import com.timo_noordzee.novi.backend.projection.VehiclePapersWithoutDataProjection;
import net.datafaker.Faker;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VehiclePapersTestUtils {

    private final Faker faker;

    public VehiclePapersTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public VehiclePapersEntity generateMockEntity() {
        return generateMockEntity(null);
    }

    public VehiclePapersEntity generateMockEntity(final VehicleEntity vehicleEntity) {
        return VehiclePapersEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.file().fileName())
                .type("application/pdf")
                .data(faker.lorem().paragraph().getBytes())
                .uploadedAt(faker.date().past(10, TimeUnit.DAYS))
                .vehicle(vehicleEntity)
                .build();
    }

    public VehiclePapersEntity generateMockEntityWithoutData() {
        return VehiclePapersEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.file().fileName())
                .type("application/pdf")
                .uploadedAt(faker.date().past(10, TimeUnit.DAYS))
                .build();
    }

    public CreateVehiclePapersDto generateMockCreateDto(final String vehicleId) {
        return CreateVehiclePapersDto.builder()
                .name(faker.file().fileName())
                .type("application/pdf")
                .data(faker.lorem().paragraph().getBytes())
                .vehicleId(vehicleId)
                .build();
    }

    public MockMultipartFile generateMockMultipartFile(final String type) {
        return new MockMultipartFile("file", generateMockFileName(), type, faker.lorem().paragraph().getBytes());
    }

    public VehiclePapersWithoutDataProjection generateMockVehiclePapersWithoutDataProjection() {
        final UUID id = UUID.randomUUID();
        final String name = generateMockFileName();
        final Date uploadedAt = faker.date().past(10, TimeUnit.DAYS);
        return new VehiclePapersWithoutDataProjection() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getType() {
                return "application/pdf";
            }

            @Override
            public Date getUploadedAt() {
                return uploadedAt;
            }
        };
    }

    public String generateMockFileName() {
        return faker.file().fileName(null, null, "pdf", null);
    }

}
