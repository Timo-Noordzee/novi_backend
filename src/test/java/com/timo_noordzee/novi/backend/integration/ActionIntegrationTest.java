package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import com.timo_noordzee.novi.backend.repository.ActionRepository;
import com.timo_noordzee.novi.backend.util.ActionTestUtils;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ActionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActionRepository actionRepository;

    private final ActionTestUtils actionTestUtils = new ActionTestUtils();

    @Test
    void getAllWorksThroughAllLayers() throws Exception {
        final List<ActionEntity> actionEntityList = new ArrayList<>();
        actionEntityList.add(actionTestUtils.generateMockEntity());
        actionEntityList.add(actionTestUtils.generateMockEntity());
        actionRepository.save(actionEntityList.get(0));
        actionRepository.save(actionEntityList.get(1));

        mockMvc.perform(MockMvcRequestBuilders.get("/actions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(actionEntityList.get(0).getId().toString())))
                .andExpect(jsonPath("$[0].name", Is.is(actionEntityList.get(0).getName())))
                .andExpect(jsonPath("$[0].price", Is.is(actionEntityList.get(0).getPrice())))
                .andExpect(jsonPath("$[1].id", Is.is(actionEntityList.get(1).getId().toString())))
                .andExpect(jsonPath("$[1].name", Is.is(actionEntityList.get(1).getName())))
                .andExpect(jsonPath("$[1].price", Is.is(actionEntityList.get(1).getPrice())));
    }

    @Test
    void addActionAndGetByIdWorksThroughAllLayers() throws Exception {
        final CreateActionDto createActionDto = actionTestUtils.generateMockCreateDto();
        final String payload = objectMapper.writeValueAsString(createActionDto);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", Is.is(createActionDto.getName())))
                .andExpect(jsonPath("$.price", Is.is(createActionDto.getPrice())))
                .andReturn();

        final String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
        assertThat(id).isNotEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/actions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(id)))
                .andExpect(jsonPath("$.name", Is.is(createActionDto.getName())))
                .andExpect(jsonPath("$.price", Is.is(createActionDto.getPrice())));
    }

    @Test
    void updateActionWorksThroughAllLayers() throws Exception {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        actionRepository.save(actionEntity);
        final UpdateActionDto updateActionDto = actionTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateActionDto);
        final String id = actionEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/actions/{id}", id)
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(id)))
                .andExpect(jsonPath("$.name", Is.is(updateActionDto.getName())))
                .andExpect(jsonPath("$.price", Is.is(updateActionDto.getPrice())));
    }

    @Test
    void deleteActionWorksThroughAllLayers() throws Exception {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        actionRepository.save(actionEntity);
        final String id = actionEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/actions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(actionEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(actionEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(actionEntity.getPrice())));
    }

}