package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.repository.RepairRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.RepairTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RepairIntegrationTest {

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final RepairTestUtils repairTestUtils = new RepairTestUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(vehicleRepository).isNotNull();
        assertThat(repairRepository).isNotNull();
    }

    @Test
    void addRepairAndGetByIdWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final CreateRepairDto createRepairDto = repairTestUtils.generateMockCreateDto(vehicleEntity.getVin());
        final String payload = objectMapper.writeValueAsString(createRepairDto);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/repairs")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.remarks", Is.is(createRepairDto.getRemarks())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andReturn();

        final String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
        assertThat(id).isNotEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/repairs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(id)))
                .andExpect(jsonPath("$.remarks", Is.is(createRepairDto.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(createRepairDto.getStatus())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())));
    }

    @Test
    void updateRepairWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);
        final UpdateRepairDto updateRepairDto = repairTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateRepairDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/repairs/{id}", repairEntity.getId().toString())
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(repairEntity.getId().toString())))
                .andExpect(jsonPath("$.remarks", Is.is(updateRepairDto.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(updateRepairDto.getStatus())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())));
    }

    @Test
    void getAllRepairsWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity1 = repairTestUtils.generateMockEntity(vehicleEntity);
        final RepairEntity repairEntity2 = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity1);
        repairRepository.save(repairEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/repairs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(repairEntity1.getId().toString())))
                .andExpect(jsonPath("$[0].remarks", Is.is(repairEntity1.getRemarks())))
                .andExpect(jsonPath("$[0].status", Is.is(repairEntity1.getStatus().getValue())))
                .andExpect(jsonPath("$[0].vehicle").doesNotExist())
                .andExpect(jsonPath("$[1].id", Is.is(repairEntity2.getId().toString())))
                .andExpect(jsonPath("$[1].remarks", Is.is(repairEntity2.getRemarks())))
                .andExpect(jsonPath("$[1].status", Is.is(repairEntity2.getStatus().getValue())))
                .andExpect(jsonPath("$[1].vehicle").doesNotExist());
    }

    @Test
    void deleteVehicleWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/repairs/{id}", repairEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(repairEntity.getId().toString())))
                .andExpect(jsonPath("$.remarks", Is.is(repairEntity.getRemarks())))
                .andExpect(jsonPath("$.status", Is.is(repairEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())));
    }

}
