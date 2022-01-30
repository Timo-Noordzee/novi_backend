package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.*;
import com.timo_noordzee.novi.backend.domain.RepairLineType;
import com.timo_noordzee.novi.backend.dto.AddRepairLinesDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairLineDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.OutOfStockException;
import com.timo_noordzee.novi.backend.repository.*;
import com.timo_noordzee.novi.backend.util.*;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RepairLineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private RepairLineRepository repairLineRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private ActionRepository actionRepository;

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final RepairTestUtils repairTestUtils = new RepairTestUtils();
    private final RepairLineTestUtils repairLineTestUtils = new RepairLineTestUtils();
    private final PartTestUtils partTestUtils = new PartTestUtils();
    private final ActionTestUtils actionTestUtils = new ActionTestUtils();

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(vehicleRepository).isNotNull();
        assertThat(repairRepository).isNotNull();
        assertThat(repairLineRepository).isNotNull();
        assertThat(partRepository).isNotNull();
        assertThat(actionRepository).isNotNull();
    }

    @Test
    void rollbackWhenOutOfStockExceptionDuringAddLinesToRepairWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);
        final PartEntity partEntity1 = partTestUtils.generateMockEntity(2);
        partRepository.save(partEntity1);
        final PartEntity partEntity2 = partTestUtils.generateMockEntity(0);
        partRepository.save(partEntity2);
        final AddRepairLinesDto.Part addPartLine1 = repairLineTestUtils.generateMockPartLineDto(partEntity1, 2);
        final AddRepairLinesDto.Part addPartLine2 = repairLineTestUtils.generateMockPartLineDto(partEntity2, 1);
        final AddRepairLinesDto addRepairLinesDto = repairLineTestUtils.generateMockDto(addPartLine1, addPartLine2);
        final String payload = objectMapper.writeValueAsString(addRepairLinesDto);
        final String id = repairEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/repairs/{id}/lines", id)
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(OutOfStockException.ERROR_CODE)));

        final PartEntity partEntity1After = partRepository.findById(partEntity1.getId()).orElse(null);

        assertThat(partEntity1After).isNotNull();
        assertThat(partEntity1After.getStock()).isEqualTo(partEntity1.getStock());
    }

    @Test
    void rollbackWhenEntityNotFoundExceptionDuringAddLinesToRepairWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);
        final PartEntity partEntity1 = partTestUtils.generateMockEntity(20);
        partRepository.save(partEntity1);
        final PartEntity partEntity2 = partTestUtils.generateMockEntity(20);
        partRepository.save(partEntity2);
        final AddRepairLinesDto.Part addPartLine1 = repairLineTestUtils.generateMockPartLineDto(partEntity1, 2);
        final AddRepairLinesDto.Part addPartLine2 = repairLineTestUtils.generateMockPartLineDto(partEntity2, 1);
        final AddRepairLinesDto.Action addActionLine = repairLineTestUtils.generateMockActionLineDto(UUID.randomUUID().toString(), 1);
        final AddRepairLinesDto addRepairLinesDto = repairLineTestUtils.generateMockDto(addPartLine1, addPartLine2);
        addRepairLinesDto.setActions(Collections.singletonList(addActionLine));
        final String payload = objectMapper.writeValueAsString(addRepairLinesDto);
        final String id = repairEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/repairs/{id}/lines", id)
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));

        final PartEntity partEntity1After = partRepository.findById(partEntity1.getId()).orElse(null);

        assertThat(partEntity1After).isNotNull();
        assertThat(partEntity1After.getStock()).isEqualTo(partEntity1.getStock());
    }

    @Test
    void addRepairLinesWorksTroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);
        final PartEntity partEntity = partTestUtils.generateMockEntity(10);
        partRepository.save(partEntity);
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        actionRepository.save(actionEntity);
        final AddRepairLinesDto.Part addPartLineDto1 = repairLineTestUtils.generateMockPartLineDto(partEntity, 4);
        final AddRepairLinesDto.Part addPartLineDto2 = repairLineTestUtils.generateMockPartLineDto(partEntity, 2);
        final AddRepairLinesDto.Action addActionLineDto = repairLineTestUtils.generateMockActionLineDto(actionEntity, 2);
        final AddRepairLinesDto.Custom addCustomLineDto = repairLineTestUtils.generateMockCustomLineDto(RepairLineType.ACTION.getValue());
        final AddRepairLinesDto addRepairLinesDto = repairLineTestUtils.generateMockDto(
                Arrays.asList(addPartLineDto1, addPartLineDto2),
                Collections.singletonList(addActionLineDto),
                Collections.singletonList(addCustomLineDto)
        );
        final String payload = objectMapper.writeValueAsString(addRepairLinesDto);
        final String id = repairEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/repairs/{id}/lines", id)
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(repairEntity.getId().toString())))
                .andExpect(jsonPath("$.remarks", Is.is(repairEntity.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(repairEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.lines", hasSize(3)))
                .andExpect(jsonPath("$.lines[0].name", Is.is(partEntity.getName())))
                .andExpect(jsonPath("$.lines[0].price", Is.is(partEntity.getPrice())))
                .andExpect(jsonPath("$.lines[0].type", Is.is(RepairLineType.PART.getValue())))
                .andExpect(jsonPath("$.lines[0].amount", Is.is(addPartLineDto1.getAmount() + addPartLineDto2.getAmount())))
                .andExpect(jsonPath("$.lines[1].name", Is.is(actionEntity.getName())))
                .andExpect(jsonPath("$.lines[1].price", Is.is(actionEntity.getPrice())))
                .andExpect(jsonPath("$.lines[1].type", Is.is(RepairLineType.ACTION.getValue())))
                .andExpect(jsonPath("$.lines[2].name", Is.is(addCustomLineDto.getName())))
                .andExpect(jsonPath("$.lines[2].price", Is.is(addCustomLineDto.getPrice())))
                .andExpect(jsonPath("$.lines[2].type", Is.is(addCustomLineDto.getType())));

        final PartEntity partEntityAfter = partRepository.findById(partEntity.getId()).orElse(null);

        assertThat(partEntityAfter).isNotNull();
        assertThat(partEntityAfter.getStock()).isEqualTo(partEntity.getStock() - addPartLineDto1.getAmount() - addPartLineDto2.getAmount());
    }

    @Test
    void updateRepairLineWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);
        final RepairLineEntity repairLineEntity = repairLineTestUtils.generateMockEntity(repairEntity);
        repairLineRepository.save(repairLineEntity);
        final UpdateRepairLineDto updateRepairLineDto = repairLineTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateRepairLineDto);
        final String id = repairEntity.getId().toString();
        final String lineId = repairLineEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/repairs/{id}/lines/{lineId}", id, lineId)
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(repairLineEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(updateRepairLineDto.getName())))
                .andExpect(jsonPath("$.price", Is.is(updateRepairLineDto.getPrice())))
                .andExpect(jsonPath("$.amount", Is.is(updateRepairLineDto.getAmount())))
                .andExpect(jsonPath("$.type", Is.is(updateRepairLineDto.getType())));
    }

    @Test
    void deleteRepairLineWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);
        final RepairLineEntity repairLineEntity = repairLineTestUtils.generateMockEntity(repairEntity);
        repairLineRepository.save(repairLineEntity);
        final String id = repairEntity.getId().toString();
        final String lineId = repairLineEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/repairs/{id}/lines/{lineId}", id, lineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(repairLineEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(repairLineEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(repairLineEntity.getPrice())))
                .andExpect(jsonPath("$.amount", Is.is(repairLineEntity.getAmount())))
                .andExpect(jsonPath("$.type", Is.is(repairLineEntity.getType().getValue())));
    }

}
