package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.dto.CreatePartDto;
import com.timo_noordzee.novi.backend.dto.UpdatePartDto;
import com.timo_noordzee.novi.backend.repository.PartRepository;
import com.timo_noordzee.novi.backend.util.PartTestUtils;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.timo_noordzee.novi.backend.domain.Role.ROLE_BACKOFFICE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@WithMockUser(roles = {ROLE_BACKOFFICE})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PartRepository partRepository;

    private final PartTestUtils partTestUtils = new PartTestUtils();

    @Test
    void getAllWorksThroughAllLayers() throws Exception {
        final List<PartEntity> partEntityList = new ArrayList<>();
        partEntityList.add(partTestUtils.generateMockEntity());
        partEntityList.add(partTestUtils.generateMockEntity());
        partRepository.save(partEntityList.get(0));
        partRepository.save(partEntityList.get(1));

        mockMvc.perform(MockMvcRequestBuilders.get("/parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(partEntityList.get(0).getId().toString())))
                .andExpect(jsonPath("$[0].name", Is.is(partEntityList.get(0).getName())))
                .andExpect(jsonPath("$[0].price", Is.is(partEntityList.get(0).getPrice())))
                .andExpect(jsonPath("$[0].stock", Is.is(partEntityList.get(0).getStock())))
                .andExpect(jsonPath("$[1].id", Is.is(partEntityList.get(1).getId().toString())))
                .andExpect(jsonPath("$[1].name", Is.is(partEntityList.get(1).getName())))
                .andExpect(jsonPath("$[1].price", Is.is(partEntityList.get(1).getPrice())))
                .andExpect(jsonPath("$[1].stock", Is.is(partEntityList.get(1).getStock())));
    }

    @Test
    void addPartAndGetByIdWorksThroughAllLayers() throws Exception {
        final CreatePartDto createPartDto = partTestUtils.generateMockCreateDto();
        final String payload = objectMapper.writeValueAsString(createPartDto);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", Is.is(createPartDto.getName())))
                .andExpect(jsonPath("$.price", Is.is(createPartDto.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(createPartDto.getStock())))
                .andReturn();

        final String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
        assertThat(id).isNotEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/parts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(id)))
                .andExpect(jsonPath("$.name", Is.is(createPartDto.getName())))
                .andExpect(jsonPath("$.price", Is.is(createPartDto.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(createPartDto.getStock())));
    }

    @Test
    void updatePartWorksThroughAllLayers() throws Exception {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        partRepository.save(partEntity);
        final UpdatePartDto updatePartDto = partTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updatePartDto);
        final String id = partEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/parts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(id)))
                .andExpect(jsonPath("$.name", Is.is(updatePartDto.getName())))
                .andExpect(jsonPath("$.price", Is.is(updatePartDto.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(updatePartDto.getStock())));
    }

    @Test
    void deletePartWorksThroughAllLayers() throws Exception {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        partRepository.save(partEntity);
        final String id = partEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/parts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(partEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(partEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(partEntity.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(partEntity.getStock())));
    }

}