package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import com.timo_noordzee.novi.backend.repository.ShortcomingRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.ShortcomingTestUtils;
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
public class ShortcomingIntegrationTest {

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final ShortcomingTestUtils shortcomingTestUtils = new ShortcomingTestUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ShortcomingRepository shortcomingRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(vehicleRepository).isNotNull();
        assertThat(shortcomingRepository).isNotNull();
    }

    @Test
    void addShortcomingAndGetByIdWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final CreateShortcomingDto createShortcomingDto = shortcomingTestUtils.generateMockCreateDto(vehicleEntity.getVin());
        final String payload = objectMapper.writeValueAsString(createShortcomingDto);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shortcomings")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description", Is.is(createShortcomingDto.getDescription())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())))
                .andReturn();

        final String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
        assertThat(id).isNotEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/shortcomings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(id)))
                .andExpect(jsonPath("$.description", Is.is(createShortcomingDto.getDescription())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())));
    }

    @Test
    void updateShortcomingWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        shortcomingRepository.save(shortcomingEntity);
        final UpdateShortcomingDto updateShortcomingDto = shortcomingTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateShortcomingDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/shortcomings/{id}", shortcomingEntity.getId().toString())
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(shortcomingEntity.getId().toString())))
                .andExpect(jsonPath("$.description", Is.is(updateShortcomingDto.getDescription())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())));
    }

    @Test
    void getAllShortcomingsWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final ShortcomingEntity shortcomingEntity1 = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        final ShortcomingEntity shortcomingEntity2 = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        shortcomingRepository.save(shortcomingEntity1);
        shortcomingRepository.save(shortcomingEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/shortcomings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(shortcomingEntity1.getId().toString())))
                .andExpect(jsonPath("$[0].description", Is.is(shortcomingEntity1.getDescription())))
                .andExpect(jsonPath("$[0].vehicle").doesNotExist())
                .andExpect(jsonPath("$[1].id", Is.is(shortcomingEntity2.getId().toString())))
                .andExpect(jsonPath("$[1].description", Is.is(shortcomingEntity2.getDescription())))
                .andExpect(jsonPath("$[1].vehicle").doesNotExist());
    }

    @Test
    void deleteVehicleWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        shortcomingRepository.save(shortcomingEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/shortcomings/{id}", shortcomingEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(shortcomingEntity.getId().toString())))
                .andExpect(jsonPath("$.description", Is.is(shortcomingEntity.getDescription())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.vehicle.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.vehicle.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.vehicle.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.vehicle.year", Is.is(vehicleEntity.getYear())));
    }

}
