package com.timo_noordzee.novi.backend.integration;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.repository.VehiclePapersRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.VehiclePapersTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;

import static com.timo_noordzee.novi.backend.domain.Role.ROLE_ADMINISTRATIVE;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@WithMockUser(roles = {ROLE_ADMINISTRATIVE})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class VehiclePapersIntegrationTest {

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final VehiclePapersTestUtils vehiclePapersTestUtils = new VehiclePapersTestUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehiclePapersRepository vehiclePapersRepository;

    @Test
    void getAllVehiclesWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final VehiclePapersEntity vehiclePapersEntity1 = vehiclePapersTestUtils.generateMockEntity(vehicleEntity);
        final VehiclePapersEntity vehiclePapersEntity2 = vehiclePapersTestUtils.generateMockEntity(vehicleEntity);
        vehiclePapersRepository.save(vehiclePapersEntity1);
        vehiclePapersRepository.save(vehiclePapersEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehiclePapers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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

    @Test
    void getVehiclePapersByIdWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersTestUtils.generateMockEntity(vehicleEntity);
        vehiclePapersRepository.save(vehiclePapersEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/vehiclePapers/{id}", vehiclePapersEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(vehiclePapersEntity.getData()));
    }

    @Test
    void deleteVehiclePapersWorksThroughAllLayers() throws Exception {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        vehicleRepository.save(vehicleEntity);
        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersTestUtils.generateMockEntity(vehicleEntity);
        vehiclePapersRepository.save(vehiclePapersEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/vehiclePapers/{id}", vehiclePapersEntity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(vehiclePapersEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(vehiclePapersEntity.getName())))
                .andExpect(jsonPath("$.type", Is.is(vehiclePapersEntity.getType())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(vehiclePapersEntity.getData()))))
                .andExpect(jsonPath("$.vehicle").doesNotExist());
    }

}
