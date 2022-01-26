package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.RepairService;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.RepairTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairController.class)
public class RepairControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepairService repairService;

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final RepairTestUtils repairTestUtils = new RepairTestUtils();

    @Test
    void getAllReturnsListOfRepairs() throws Exception {
        final List<RepairEntity> repairEntities = new ArrayList<>();
        final RepairEntity firstRepair = repairTestUtils.generateMockEntity();
        final RepairEntity secondRepair = repairTestUtils.generateMockEntity();
        repairEntities.add(firstRepair);
        repairEntities.add(secondRepair);
        when(repairService.getAll()).thenReturn(repairEntities);

        mockMvc.perform(MockMvcRequestBuilders.get("/repairs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(firstRepair.getId().toString())))
                .andExpect(jsonPath("$[0].remarks", Is.is(firstRepair.getRemarks())))
                .andExpect(jsonPath("$[0].status", Is.is(firstRepair.getStatus().getValue())))
                .andExpect(jsonPath("$[0].vehicle").doesNotExist())
                .andExpect(jsonPath("$[1].id", Is.is(secondRepair.getId().toString())))
                .andExpect(jsonPath("$[1].remarks", Is.is(secondRepair.getRemarks())))
                .andExpect(jsonPath("$[1].status", Is.is(secondRepair.getStatus().getValue())))
                .andExpect(jsonPath("$[1].vehicle").doesNotExist());
    }

    @Test
    void getByIdOnNonexistentRepairReturnsEntityNotFoundException() throws Exception {
        final String id = UUID.randomUUID().toString();
        when(repairService.getById(any(String.class))).thenThrow(new EntityNotFoundException(id, RepairEntity.class.getSimpleName()));

        mockMvc.perform(MockMvcRequestBuilders.get("/repairs/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void getByIdOnExistingRepairReturnsRepairWithVehicle() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        when(repairService.getById(any(String.class))).thenReturn(repairEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/repairs/{id}", repairEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(repairEntity.getId().toString())))
                .andExpect(jsonPath("$.remarks", Is.is(repairEntity.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(repairEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.vehicle.vin", Is.is(repairEntity.getVehicle().getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(repairEntity.getVehicle().getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(repairEntity.getVehicle().getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(repairEntity.getVehicle().getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(repairEntity.getVehicle().getYear())))
                .andExpect(jsonPath("$.vehicle.owner").doesNotExist());
    }

    @Test
    void postWitInvalidPayloadReturnsValidationErrors() throws Exception {
        final CreateRepairDto createRepairDto = CreateRepairDto.builder().build();
        final String payload = objectMapper.writeValueAsString(createRepairDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/repairs")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleId").isNotEmpty());
    }

    @Test
    void postWithValidPayloadReturnsRepairEntityWithVehicle() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        when(repairService.add(any(CreateRepairDto.class))).thenReturn(repairEntity);
        final CreateRepairDto createRepairDto = repairTestUtils.generateMockCreateDto(vehicleEntity.getVin());
        final String payload = objectMapper.writeValueAsString(createRepairDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/repairs")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Is.is(repairEntity.getId().toString())))
                .andExpect(jsonPath("$.remarks", Is.is(repairEntity.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(repairEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.vehicle.owner.id", Is.is(customerEntity.getId().toString())))
                .andExpect(jsonPath("$.vehicle.owner.name", Is.is(customerEntity.getName())))
                .andExpect(jsonPath("$.vehicle.owner.email", Is.is(customerEntity.getEmail())));
    }

    @Test
    void putWithValidPayloadReturnsUpdatedRepairEntity() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        final UpdateRepairDto updateRepairDto = repairTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateRepairDto);
        final String id = UUID.randomUUID().toString();
        when(repairService.update(any(String.class), any(UpdateRepairDto.class))).thenReturn(repairEntity);

        mockMvc.perform(MockMvcRequestBuilders.put("/repairs/{id}", id)
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(repairEntity.getId().toString())))
                .andExpect(jsonPath("$.remarks", Is.is(repairEntity.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(repairEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.vehicle.createdAt").isNotEmpty());
    }

    @Test
    void deleteExistingRepairReturnsRepairEntity() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        when(repairService.deleteById(any(String.class))).thenReturn(repairEntity);
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/repairs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(repairEntity.getId().toString())))
                .andExpect(jsonPath("$.remarks", Is.is(repairEntity.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(repairEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.vehicle.createdAt").isNotEmpty());
    }

}