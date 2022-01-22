package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.LicenseTakenException;
import com.timo_noordzee.novi.backend.service.VehicleService;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VehicleController.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();


    @Test
    void getAllReturnsArrayOfVehicles() throws Exception {
        final List<VehicleEntity> vehicleEntityList = new ArrayList<>();
        final VehicleEntity firstVehicle = vehicleTestUtils.generateMockEntity();
        final VehicleEntity secondVehicle = vehicleTestUtils.generateMockEntity();
        vehicleEntityList.add(firstVehicle);
        vehicleEntityList.add(secondVehicle);
        when(vehicleService.getAll()).thenReturn(vehicleEntityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].vin", Is.is(firstVehicle.getVin())))
                .andExpect(jsonPath("$[0].license", Is.is(firstVehicle.getLicense())))
                .andExpect(jsonPath("$[0].owner").doesNotExist())
                .andExpect(jsonPath("$[1].vin", Is.is(secondVehicle.getVin())))
                .andExpect(jsonPath("$[1].license", Is.is(secondVehicle.getLicense())))
                .andExpect(jsonPath("$[1].owner").doesNotExist());
    }

    @Test
    void getByIdOnNonexistentVehicleReturnsEntityNotFoundException() throws Exception {
        final String id = vehicleTestUtils.randomVin();
        when(vehicleService.getById(any(String.class)))
                .thenThrow(new EntityNotFoundException(id, VehicleEntity.class.getSimpleName()));

        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void addingWithInvalidPayloadReturnsValidationErrors() throws Exception {
        final CreateVehicleDto createVehicleDto = CreateVehicleDto.builder()
                .year(0)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/vehicles")
                        .content(objectMapper.writeValueAsString(createVehicleDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vin").isNotEmpty())
                .andExpect(jsonPath("$.license").isNotEmpty())
                .andExpect(jsonPath("$.brand").isNotEmpty())
                .andExpect(jsonPath("$.make").isNotEmpty())
                .andExpect(jsonPath("$.year").isNotEmpty())
                .andExpect(jsonPath("$.customerId").isNotEmpty());
    }

    @Test
    void addingWithTakenLicenseReturnsEmailTakenException() throws Exception {
        final CreateVehicleDto createVehicleDto = vehicleTestUtils.generateMockCreateDto(UUID.randomUUID());
        when(vehicleService.add(any(CreateVehicleDto.class)))
                .thenThrow(new LicenseTakenException(createVehicleDto.getLicense()));

        mockMvc.perform(MockMvcRequestBuilders.post("/vehicles")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createVehicleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(LicenseTakenException.ERROR_CODE)));
    }

    @Test
    void addingWithValidPayloadReturnsVehicleEntity() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        final CreateVehicleDto createVehicleDto = vehicleTestUtils.generateMockCreateDto(customerEntity.getId());
        when(vehicleService.add(any(CreateVehicleDto.class))).thenReturn(vehicleEntity);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/vehicles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createVehicleDto)));

        resultActions.andExpect(status().isCreated());
        assertResultMatchesEntity(resultActions, vehicleEntity);
    }

    @Test
    void updatingWithValidPayloadReturnsUpdatedVehicle() throws Exception {
        final UpdateVehicleDto updateVehicleDto = vehicleTestUtils.generateMockUpdateDto();
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleService.update(any(String.class), any(UpdateVehicleDto.class))).thenReturn(vehicleEntity);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/vehicles/{id}", vehicleEntity.getVin())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateVehicleDto)));

        resultActions.andExpect(status().isOk());
        assertResultMatchesEntity(resultActions, vehicleEntity);
    }

    @Test
    void deletingExistingVehicleReturnsVehicle() throws Exception {
        final String id = vehicleTestUtils.randomVin();
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleService.deleteById(any(String.class))).thenReturn(vehicleEntity);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/vehicles/{id}", id));

        resultActions.andExpect(status().isOk());
        assertResultMatchesEntity(resultActions, vehicleEntity);
    }

    private void assertResultMatchesEntity(
            final ResultActions resultActions,
            final VehicleEntity vehicleEntity
    ) throws Exception {
        resultActions.andExpect(jsonPath("$.vin", Is.is(vehicleEntity.getVin())));
        resultActions.andExpect(jsonPath("$.license", Is.is(vehicleEntity.getLicense())));
        resultActions.andExpect(jsonPath("$.brand", Is.is(vehicleEntity.getBrand())));
        resultActions.andExpect(jsonPath("$.make", Is.is(vehicleEntity.getMake())));
        resultActions.andExpect(jsonPath("$.year", Is.is(vehicleEntity.getYear())));
        resultActions.andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

}
