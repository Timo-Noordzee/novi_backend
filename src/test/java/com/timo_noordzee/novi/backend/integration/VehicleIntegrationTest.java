package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import com.timo_noordzee.novi.backend.repository.CustomerRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
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
public class VehicleIntegrationTest {

    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(customerRepository).isNotNull();
    }

    @Test
    void addVehicleAndGetByIdWorksThroughAllLayers() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        customerRepository.save(customerEntity);
        final CreateVehicleDto createVehicleDto = vehicleTestUtils.generateMockCreateDto(customerEntity.getId());

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/vehicles")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createVehicleDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vin", Is.is(createVehicleDto.getVin())))
                .andExpect(jsonPath("$.license", Is.is(createVehicleDto.getLicense())))
                .andExpect(jsonPath("$.brand", Is.is(createVehicleDto.getBrand())))
                .andExpect(jsonPath("$.make", Is.is(createVehicleDto.getMake())))
                .andExpect(jsonPath("$.year", Is.is(createVehicleDto.getYear())))
                .andExpect(jsonPath("$.owner.id", Is.is(customerEntity.getId().toString())))
                .andReturn();

        final String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.vin");
        assertThat(id).isNotEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vin", Is.is(createVehicleDto.getVin())))
                .andExpect(jsonPath("$.license", Is.is(createVehicleDto.getLicense())))
                .andExpect(jsonPath("$.brand", Is.is(createVehicleDto.getBrand())))
                .andExpect(jsonPath("$.make", Is.is(createVehicleDto.getMake())))
                .andExpect(jsonPath("$.year", Is.is(createVehicleDto.getYear())))
                .andExpect(jsonPath("$.owner.id", Is.is(customerEntity.getId().toString())));
    }

    @Test
    void updateVehicleWorksThroughAllLayers() throws Exception {
        final CustomerEntity customerEntity1 = customerTestUtils.generateMockEntity();
        final CustomerEntity customerEntity2 = customerTestUtils.generateMockEntity();
        customerRepository.save(customerEntity1);
        customerRepository.save(customerEntity2);
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity1);
        vehicleRepository.save(vehicleEntity);
        final UpdateVehicleDto updateVehicleDto = vehicleTestUtils.generateMockUpdateDto(customerEntity2.getId().toString());

        mockMvc.perform(MockMvcRequestBuilders.put("/vehicles/{id}", vehicleEntity.getVin())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateVehicleDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.license", Is.is(updateVehicleDto.getLicense())))
                .andExpect(jsonPath("$.brand", Is.is(updateVehicleDto.getBrand())))
                .andExpect(jsonPath("$.make", Is.is(updateVehicleDto.getMake())))
                .andExpect(jsonPath("$.year", Is.is(updateVehicleDto.getYear())))
                .andExpect(jsonPath("$.owner.id", Is.is(customerEntity2.getId().toString())))
                .andExpect(jsonPath("$.owner.email", Is.is(customerEntity2.getEmail())))
                .andExpect(jsonPath("$.owner.phone", Is.is(customerEntity2.getPhone())))
                .andExpect(jsonPath("$.owner.name", Is.is(customerEntity2.getName())))
                .andExpect(jsonPath("$.owner.surname", Is.is(customerEntity2.getSurname())))
                .andReturn();
    }

    @Test
    void getAllVehiclesWorksThroughAllLayers() throws Exception {
        final CustomerEntity customerEntity1 = customerTestUtils.generateMockEntity();
        final CustomerEntity customerEntity2 = customerTestUtils.generateMockEntity();
        customerRepository.save(customerEntity1);
        customerRepository.save(customerEntity2);
        final VehicleEntity vehicleEntity1 = vehicleTestUtils.generateMockEntity(customerEntity1);
        final VehicleEntity vehicleEntity2 = vehicleTestUtils.generateMockEntity(customerEntity2);
        vehicleRepository.save(vehicleEntity1);
        vehicleRepository.save(vehicleEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].vin", Is.is(vehicleEntity1.getVin())))
                .andExpect(jsonPath("$[0].owner").doesNotExist())
                .andExpect(jsonPath("$[1].vin", Is.is(vehicleEntity2.getVin())))
                .andExpect(jsonPath("$[1].owner").doesNotExist());
    }

    @Test
    void deleteVehicleWorksThroughAllLayers() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        customerRepository.save(customerEntity);
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        vehicleRepository.save(vehicleEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/vehicles/{id}", vehicleEntity.getVin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.owner.id", Is.is(customerEntity.getId().toString())));
    }

}
