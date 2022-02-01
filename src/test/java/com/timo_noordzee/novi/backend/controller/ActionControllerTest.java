package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.ActionService;
import com.timo_noordzee.novi.backend.service.AuthUserService;
import com.timo_noordzee.novi.backend.util.ActionTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.timo_noordzee.novi.backend.domain.Role.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = {ROLE_BACKOFFICE})
@WebMvcTest(controllers = ActionController.class)
public class ActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @SuppressWarnings("unused")
    private AuthUserService authUserService;

    @MockBean
    private ActionService actionService;

    private final ActionTestUtils actionTestUtils = new ActionTestUtils();

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER})
    void makeGetRequestWithoutRequiredRoleIsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/actions"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER, ROLE_MECHANIC})
    void makePostRequestWithoutRequiredRoleIsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/actions"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER, ROLE_MECHANIC})
    void makePutRequestWithoutRequiredRoleIsForbidden() throws Exception {
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/actions/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER, ROLE_MECHANIC})
    void makeDeleteRequestWithoutRequiredRoleIsForbidden() throws Exception {
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/actions/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllActionsReturnsListOfActionEntities() throws Exception {
        final List<ActionEntity> actionEntityList = new ArrayList<>();
        actionEntityList.add(actionTestUtils.generateMockEntity());
        actionEntityList.add(actionTestUtils.generateMockEntity());
        when(actionService.getAll()).thenReturn(actionEntityList);

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
    void getByIdReturnsActionEntity() throws Exception {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        when(actionService.getById(any(String.class))).thenReturn(actionEntity);
        final String id = actionEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/actions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(actionEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(actionEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(actionEntity.getPrice())));
    }

    @Test
    void postWithInvalidPayloadReturnsValidationErrors() throws Exception {
        final CreateActionDto createActionDto = CreateActionDto.builder().price(-10.0).build();
        final String payload = objectMapper.writeValueAsString(createActionDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").isNotEmpty());
    }

    @Test
    void postWithValidPayloadReturnsActionEntity() throws Exception {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        when(actionService.add(any(CreateActionDto.class))).thenReturn(actionEntity);
        final CreateActionDto createActionDto = actionTestUtils.generateMockCreateDto();
        final String payload = objectMapper.writeValueAsString(createActionDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Is.is(actionEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(actionEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(actionEntity.getPrice())));
    }

    @Test
    void putWithValidPayloadReturnsUpdatedActionEntity() throws Exception {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        when(actionService.update(any(String.class), any(UpdateActionDto.class))).thenReturn(actionEntity);
        final UpdateActionDto updateActionDto = actionTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateActionDto);
        final String id = actionEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/actions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(jsonPath("$.id", Is.is(actionEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(actionEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(actionEntity.getPrice())));
    }

    @Test
    void deleteNonexistentActionReturnsEntityNotFoundException() throws Exception {
        when(actionService.deleteById(any(String.class))).thenAnswer(i -> {
            throw new EntityNotFoundException(i.getArgument(0), ActionEntity.class.getSimpleName());
        });
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/actions/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void deleteExistingActionReturnsDeletedActionEntity() throws Exception {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        when(actionService.deleteById(any(String.class))).thenReturn(actionEntity);
        final String id = actionEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/actions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(actionEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(actionEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(actionEntity.getPrice())));
    }

}