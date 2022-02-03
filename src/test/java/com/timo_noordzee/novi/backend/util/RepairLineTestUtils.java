package com.timo_noordzee.novi.backend.util;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.RepairLineEntity;
import com.timo_noordzee.novi.backend.domain.RepairLineType;
import com.timo_noordzee.novi.backend.dto.AddRepairLinesDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairLineDto;
import net.datafaker.Faker;

import java.util.*;

public class RepairLineTestUtils {

    private final Faker faker;

    public RepairLineTestUtils() {
        faker = new Faker(new Locale("nl"));
    }

    public RepairLineEntity generateMockEntity(final RepairEntity repairEntity) {
        final int index = new Random().nextInt(RepairLineType.values().length);
        return RepairLineEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.lorem().word())
                .amount(faker.number().numberBetween(1, 5))
                .price(faker.number().randomDouble(2, 0, 999))
                .type(RepairLineType.values()[index])
                .repair(repairEntity)
                .build();
    }

    public AddRepairLinesDto.Part generateMockPartLineDto(final PartEntity partEntity, final int amount) {
        return generateMockPartLineDto(partEntity.getId().toString(), amount);
    }

    public AddRepairLinesDto.Part generateMockPartLineDto(final String partId, final int amount) {
        return AddRepairLinesDto.Part.builder()
                .id(partId)
                .amount(amount)
                .build();
    }

    public AddRepairLinesDto.Action generateMockActionLineDto(final ActionEntity actionEntity, final int amount) {
        return generateMockActionLineDto(actionEntity.getId().toString(), amount);
    }

    public AddRepairLinesDto.Action generateMockActionLineDto(final String actionId, final int amount) {
        return AddRepairLinesDto.Action.builder()
                .id(actionId)
                .amount(amount)
                .build();
    }

    public AddRepairLinesDto.Custom generateMockCustomLineDto() {
        final int index = new Random().nextInt(RepairLineType.values().length);
        return generateMockCustomLineDto(RepairLineType.values()[index].getValue());
    }

    public AddRepairLinesDto.Custom generateMockCustomLineDto(final int repairLineType) {
        return AddRepairLinesDto.Custom.builder()
                .name(faker.lorem().word())
                .amount(faker.number().numberBetween(1, 5))
                .price(faker.number().randomDouble(2, 0, 999))
                .type(repairLineType)
                .build();
    }

    public AddRepairLinesDto generateMockDto(final AddRepairLinesDto.Part... parts) {
        return generateMockDto(Arrays.asList(parts), new ArrayList<>(), new ArrayList<>());
    }

    public AddRepairLinesDto generateMockDto(final AddRepairLinesDto.Action... actions) {
        return generateMockDto(new ArrayList<>(), Arrays.asList(actions), new ArrayList<>());
    }

    public AddRepairLinesDto generateMockDto(final AddRepairLinesDto.Custom... custom) {
        return generateMockDto(new ArrayList<>(), new ArrayList<>(), Arrays.asList(custom));
    }

    public AddRepairLinesDto generateMockDto(
            final List<AddRepairLinesDto.Part> parts,
            final List<AddRepairLinesDto.Action> actions,
            final List<AddRepairLinesDto.Custom> custom
    ) {
        return AddRepairLinesDto.builder()
                .parts(parts)
                .actions(actions)
                .custom(custom)
                .build();
    }

    public UpdateRepairLineDto generateMockUpdateDto() {
        final int index = new Random().nextInt(RepairLineType.values().length);
        return UpdateRepairLineDto.builder()
                .name(faker.lorem().word())
                .price(faker.number().randomDouble(2, 0, 999))
                .amount(faker.number().numberBetween(1, 5))
                .type(RepairLineType.values()[index].getValue())
                .build();
    }

}
