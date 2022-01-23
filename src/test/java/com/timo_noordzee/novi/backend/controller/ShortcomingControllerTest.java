package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.ShortcomingService;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.ShortcomingTestUtils;
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

@WebMvcTest(controllers = ShortcomingController.class)
public class ShortcomingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShortcomingService shortcomingService;

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final ShortcomingTestUtils shortcomingTestUtils = new ShortcomingTestUtils();

    @Test
    void getAllReturnsListOfShortcomings() throws Exception {
        final List<ShortcomingEntity> shortcomingEntities = new ArrayList<>();
        final ShortcomingEntity firstShortcoming = shortcomingTestUtils.generateMockEntity();
        final ShortcomingEntity secondShortcoming = shortcomingTestUtils.generateMockEntity();
        shortcomingEntities.add(firstShortcoming);
        shortcomingEntities.add(secondShortcoming);
        when(shortcomingService.getAll()).thenReturn(shortcomingEntities);

        mockMvc.perform(MockMvcRequestBuilders.get("/shortcomings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(firstShortcoming.getId().toString())))
                .andExpect(jsonPath("$[0].description", Is.is(firstShortcoming.getDescription())))
                .andExpect(jsonPath("$[0].vehicle").doesNotExist())
                .andExpect(jsonPath("$[1].id", Is.is(secondShortcoming.getId().toString())))
                .andExpect(jsonPath("$[1].description", Is.is(secondShortcoming.getDescription())))
                .andExpect(jsonPath("$[1].vehicle").doesNotExist());
    }

    @Test
    void getByIdOnNonexistentShortcomingReturnsEntityNotFoundException() throws Exception {
        final String id = UUID.randomUUID().toString();
        when(shortcomingService.getById(any(String.class)))
                .thenThrow(new EntityNotFoundException(id, ShortcomingEntity.class.getSimpleName()));

        mockMvc.perform(MockMvcRequestBuilders.get("/shortcomings/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void getByIdOnExistingShortcomingReturnsShortcomingWithVehicle() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        when(shortcomingService.getById(any(String.class))).thenReturn(shortcomingEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/shortcomings/{id}", shortcomingEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(shortcomingEntity.getId().toString())))
                .andExpect(jsonPath("$.description", Is.is(shortcomingEntity.getDescription())))
                .andExpect(jsonPath("$.vehicle.vin", Is.is(shortcomingEntity.getVehicle().getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(shortcomingEntity.getVehicle().getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(shortcomingEntity.getVehicle().getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(shortcomingEntity.getVehicle().getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(shortcomingEntity.getVehicle().getYear())))
                .andExpect(jsonPath("$.vehicle.owner").doesNotExist());
    }

    @Test
    void postWitInvalidPayloadReturnsValidationErrors() throws Exception {
        final CreateShortcomingDto createShortcomingDto = CreateShortcomingDto.builder().build();
        final String payload = objectMapper.writeValueAsString(createShortcomingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/shortcomings")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.vehicleId").isNotEmpty());
    }

    @Test
    void postWithValidPayloadReturnsShortcomingEntityWithVehicle() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        when(shortcomingService.add(any(CreateShortcomingDto.class))).thenReturn(shortcomingEntity);
        final CreateShortcomingDto createShortcomingDto = shortcomingTestUtils.generateMockCreateDto(vehicleEntity.getVin());
        final String payload = objectMapper.writeValueAsString(createShortcomingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/shortcomings")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Is.is(shortcomingEntity.getId().toString())))
                .andExpect(jsonPath("$.description", Is.is(shortcomingEntity.getDescription())))
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
    void putWithValidPayloadReturnsValidationErrors() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        final UpdateShortcomingDto updateShortcomingDto = shortcomingTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateShortcomingDto);
        final String id = UUID.randomUUID().toString();
        when(shortcomingService.update(any(String.class), any(UpdateShortcomingDto.class))).thenReturn(shortcomingEntity);

        mockMvc.perform(MockMvcRequestBuilders.put("/shortcomings/{id}", id)
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(shortcomingEntity.getId().toString())))
                .andExpect(jsonPath("$.description", Is.is(shortcomingEntity.getDescription())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.vehicle.createdAt").isNotEmpty());
    }

    @Test
    void deleteExistingShortcomingReturnsShortcomingEntity() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        when(shortcomingService.deleteById(any(String.class))).thenReturn(shortcomingEntity);
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/shortcomings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(shortcomingEntity.getId().toString())))
                .andExpect(jsonPath("$.description", Is.is(shortcomingEntity.getDescription())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.vehicle.createdAt").isNotEmpty());
    }

}
