package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.InvoiceEntity;
import com.timo_noordzee.novi.backend.domain.InvoiceStatus;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import com.timo_noordzee.novi.backend.projection.InvoiceWithoutDataProjection;
import net.datafaker.Faker;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InvoiceTestUtils {

    private final Faker faker;

    public InvoiceTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public InvoiceStatus randomStatus() {
        final int index = new Random().nextInt(InvoiceStatus.values().length);
        return InvoiceStatus.values()[index];
    }

    public InvoiceEntity generateMockEntity() {
        return generateMockEntity(faker.lorem().paragraph().getBytes());
    }

    public InvoiceEntity generateMockEntity(final byte[] data) {
        final InvoiceStatus invoiceStatus = randomStatus();
        final Date createdAt = faker.date().past(10, TimeUnit.DAYS);
        if (invoiceStatus == InvoiceStatus.OPEN) {
            return InvoiceEntity.builder()
                    .id(UUID.randomUUID())
                    .status(invoiceStatus)
                    .data(data)
                    .createdAt(createdAt)
                    .build();
        } else {
            return InvoiceEntity.builder()
                    .id(UUID.randomUUID())
                    .paidAt(faker.date().between(createdAt, new Date()))
                    .status(invoiceStatus)
                    .data(data)
                    .createdAt(createdAt)
                    .build();
        }
    }

    public CreateInvoiceDto generateMockCreateDto(final String repairId) {
        return CreateInvoiceDto.builder()
                .repairId(repairId)
                .build();
    }

    public UpdateInvoiceDto generateMockUpdateDto() {
        final InvoiceStatus invoiceStatus = randomStatus();
        return UpdateInvoiceDto.builder()
                .paidAt(faker.date().past(10, TimeUnit.DAYS))
                .status(invoiceStatus.getValue())
                .build();
    }

    public InvoiceWithoutDataProjection generateMockProjection() {
        final InvoiceEntity invoiceEntity = generateMockEntity();
        return new InvoiceWithoutDataProjection() {
            @Override
            public UUID getId() {
                return invoiceEntity.getId();
            }

            @Override
            public Date getCreatedAt() {
                return invoiceEntity.getCreatedAt();
            }

            @Override
            public Date getPaidAt() {
                return invoiceEntity.getPaidAt();
            }

            @Override
            public InvoiceStatus getStatus() {
                return invoiceEntity.getStatus();
            }
        };
    }
}
