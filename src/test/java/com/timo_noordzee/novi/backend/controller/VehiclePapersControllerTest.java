package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.VehiclePapersService;
import com.timo_noordzee.novi.backend.util.VehiclePapersTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VehiclePapersController.class)
public class VehiclePapersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehiclePapersService vehiclePapersService;

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final VehiclePapersTestUtils vehiclePapersTestUtils = new VehiclePapersTestUtils();

    @Test
    void getAllReturnsArrayOfVehicles() throws Exception {
        final List<VehiclePapersEntity> vehiclePapersEntityList = new ArrayList<>();
        final VehiclePapersEntity firstVehiclePapers = vehiclePapersTestUtils.generateMockEntityWithoutData();
        final VehiclePapersEntity secondVehiclePapers = vehiclePapersTestUtils.generateMockEntityWithoutData();
        vehiclePapersEntityList.add(firstVehiclePapers);
        vehiclePapersEntityList.add(secondVehiclePapers);
        when(vehiclePapersService.getAll()).thenReturn(vehiclePapersEntityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehiclePapers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(firstVehiclePapers.getId().toString())))
                .andExpect(jsonPath("$[0].name", Is.is(firstVehiclePapers.getName())))
                .andExpect(jsonPath("$[0].type", Is.is(firstVehiclePapers.getType())))
                .andExpect(jsonPath("$[0].data").doesNotExist())
                .andExpect(jsonPath("$[0].vehicle").doesNotExist())
                .andExpect(jsonPath("$[1].id", Is.is(secondVehiclePapers.getId().toString())))
                .andExpect(jsonPath("$[1].name", Is.is(secondVehiclePapers.getName())))
                .andExpect(jsonPath("$[1].type", Is.is(secondVehiclePapers.getType())))
                .andExpect(jsonPath("$[1].data").doesNotExist())
                .andExpect(jsonPath("$[1].vehicle").doesNotExist());
    }

    @Test
    void getByIdOnNonexistentVehicleReturnsEntityNotFoundException() throws Exception {
        final String id = UUID.randomUUID().toString();
        when(vehiclePapersService.getById(any(String.class)))
                .thenThrow(new EntityNotFoundException(id, VehiclePapersEntity.class.getSimpleName()));

        mockMvc.perform(MockMvcRequestBuilders.get("/vehiclePapers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void getByIdOnExistingVehiclePapersReturnsFileResponse() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersTestUtils.generateMockEntity(vehicleEntity);
        when(vehiclePapersService.getById(any(String.class))).thenReturn(vehiclePapersEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehiclePapers/{id}", vehiclePapersEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(vehiclePapersEntity.getData()));
    }

    @Test
    void deletingExistingVehiclePapersReturnsVehiclePapersEntity() throws Exception {
        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersTestUtils.generateMockEntity();
        when(vehiclePapersService.deleteById(any(String.class))).thenReturn(vehiclePapersEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/vehiclePapers/{id}", vehiclePapersEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(vehiclePapersEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(vehiclePapersEntity.getName())))
                .andExpect(jsonPath("$.type", Is.is(vehiclePapersEntity.getType())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(vehiclePapersEntity.getData()))))
                .andExpect(jsonPath("$.vehicle").doesNotExist());
    }

}
