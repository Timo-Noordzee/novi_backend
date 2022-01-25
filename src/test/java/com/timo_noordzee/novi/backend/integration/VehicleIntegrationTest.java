package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import com.timo_noordzee.novi.backend.exception.ForbiddenFileTypeException;
import com.timo_noordzee.novi.backend.repository.CustomerRepository;
import com.timo_noordzee.novi.backend.repository.ShortcomingRepository;
import com.timo_noordzee.novi.backend.repository.VehiclePapersRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.ShortcomingTestUtils;
import com.timo_noordzee.novi.backend.util.VehiclePapersTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;

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
    private final ShortcomingTestUtils shortcomingTestUtils = new ShortcomingTestUtils();
    private final VehiclePapersTestUtils vehiclePapersTestUtils = new VehiclePapersTestUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ShortcomingRepository shortcomingRepository;

    @Autowired
    private VehiclePapersRepository vehiclePapersRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(customerRepository).isNotNull();
        assertThat(vehicleRepository).isNotNull();
        assertThat(shortcomingRepository).isNotNull();
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
                .andExpect(jsonPath("$.owner.surname", Is.is(customerEntity2.getSurname())));
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

    @Test
    void getByIdWorksThroughAllLayers() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        customerRepository.save(customerEntity);
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        vehicleRepository.save(vehicleEntity);
        final ShortcomingEntity shortcomingEntity1 = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        final ShortcomingEntity shortcomingEntity2 = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        shortcomingRepository.save(shortcomingEntity1);
        shortcomingRepository.save(shortcomingEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles/{id}", vehicleEntity.getVin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vin", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.license", Is.is(vehicleEntity.getLicense())))
                .andExpect(jsonPath("$.brand", Is.is(vehicleEntity.getBrand())))
                .andExpect(jsonPath("$.make", Is.is(vehicleEntity.getMake())))
                .andExpect(jsonPath("$.year", Is.is(vehicleEntity.getYear())))
                .andExpect(jsonPath("$.owner.id", Is.is(customerEntity.getId().toString())))
                .andExpect(jsonPath("$.owner.vehicles").doesNotExist())
                .andExpect(jsonPath("$.shortcomings", hasSize(2)))
                .andExpect(jsonPath("$.shortcomings[0].id", Is.is(shortcomingEntity1.getId().toString())))
                .andExpect(jsonPath("$.shortcomings[0].description", Is.is(shortcomingEntity1.getDescription())))
                .andExpect(jsonPath("$.shortcomings[0].vehicle", Is.is(vehicleEntity.getVin())))
                .andExpect(jsonPath("$.shortcomings[1].id", Is.is(shortcomingEntity2.getId().toString())))
                .andExpect(jsonPath("$.shortcomings[1].description", Is.is(shortcomingEntity2.getDescription())))
                .andExpect(jsonPath("$.shortcomings[1].vehicle", Is.is(vehicleEntity.getVin())));
    }

    @Test
    void addVehiclePapersWithForbiddenFileTypeReturnsForbiddenFileTypeException() throws Exception {
        final String vehicleId = vehicleTestUtils.randomVin();
        final MockMultipartFile multipartFile = vehiclePapersTestUtils.generateMockMultipartFile("application/png");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/vehicles/{id}/papers", vehicleId).file(multipartFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(ForbiddenFileTypeException.ERROR_CODE)));
    }

    @Test
    void addVehiclePapersWorksThroughAllLayers() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        customerRepository.save(customerEntity);
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        vehicleRepository.save(vehicleEntity);
        final MockMultipartFile multipartFile = vehiclePapersTestUtils.generateMockMultipartFile("application/pdf");
        final int startIndex = multipartFile.getOriginalFilename().replaceAll("\\\\", "/").lastIndexOf("/");
        final String expectedFileName = multipartFile.getOriginalFilename().substring(startIndex + 1);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/vehicles/{id}/papers", vehicleEntity.getVin()).file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", Is.is(expectedFileName)))
                .andExpect(jsonPath("$.type", Is.is(multipartFile.getContentType())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(multipartFile.getBytes()))))
                .andExpect(jsonPath("$.vehicle.vin", Is.is(vehicleEntity.getVin())));
    }

    @Test
    void getPapersForVehicleWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final VehiclePapersEntity vehiclePapersEntity1 = vehiclePapersTestUtils.generateMockEntity(vehicleEntity);
        final VehiclePapersEntity vehiclePapersEntity2 = vehiclePapersTestUtils.generateMockEntity(vehicleEntity);
        vehiclePapersRepository.save(vehiclePapersEntity1);
        vehiclePapersRepository.save(vehiclePapersEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehicles/{id}/papers", vehicleEntity.getVin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(vehiclePapersEntity1.getId().toString())))
                .andExpect(jsonPath("$[0].name", Is.is(vehiclePapersEntity1.getName())))
                .andExpect(jsonPath("$[0].type", Is.is(vehiclePapersEntity1.getType())))
                .andExpect(jsonPath("$[0].data").doesNotExist())
                .andExpect(jsonPath("$[0].vehicle").doesNotExist())
                .andExpect(jsonPath("$[1].id", Is.is(vehiclePapersEntity2.getId().toString())))
                .andExpect(jsonPath("$[1].name", Is.is(vehiclePapersEntity2.getName())))
                .andExpect(jsonPath("$[1].type", Is.is(vehiclePapersEntity2.getType())))
                .andExpect(jsonPath("$[1].data").doesNotExist())
                .andExpect(jsonPath("$[1].vehicle").doesNotExist());

    }

}
