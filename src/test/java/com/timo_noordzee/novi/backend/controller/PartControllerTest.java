package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.dto.CreatePartDto;
import com.timo_noordzee.novi.backend.dto.UpdatePartDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.AuthUserService;
import com.timo_noordzee.novi.backend.service.PartService;
import com.timo_noordzee.novi.backend.util.PartTestUtils;
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
@WebMvcTest(controllers = PartController.class)
public class PartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @SuppressWarnings("unused")
    private AuthUserService authUserService;

    @MockBean
    private PartService partService;

    private final PartTestUtils partTestUtils = new PartTestUtils();

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER})
    void makeGetRequestWithoutRequiredRoleIsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/parts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER, ROLE_MECHANIC})
    void makePostRequestWithoutRequiredRoleIsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/parts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER, ROLE_MECHANIC})
    void makePutRequestWithoutRequiredRoleIsForbidden() throws Exception {
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/parts/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {ROLE_ADMINISTRATIVE, ROLE_CASHIER, ROLE_MECHANIC})
    void makeDeleteRequestWithoutRequiredRoleIsForbidden() throws Exception {
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/parts/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllPartsReturnsListOfPartEntities() throws Exception {
        final List<PartEntity> partEntityList = new ArrayList<>();
        partEntityList.add(partTestUtils.generateMockEntity());
        partEntityList.add(partTestUtils.generateMockEntity());
        when(partService.getAll()).thenReturn(partEntityList);

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
    void getByIdReturnsPartEntity() throws Exception {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        when(partService.getById(any(String.class))).thenReturn(partEntity);
        final String id = partEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/parts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(partEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(partEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(partEntity.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(partEntity.getStock())));
    }

    @Test
    void postWithInvalidPayloadReturnsValidationErrors() throws Exception {
        final CreatePartDto createPartDto = CreatePartDto.builder().stock(-1).build();
        final String payload = objectMapper.writeValueAsString(createPartDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").isNotEmpty())
                .andExpect(jsonPath("$.stock").isNotEmpty());
    }

    @Test
    void postWithValidPayloadReturnsPartEntity() throws Exception {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        when(partService.add(any(CreatePartDto.class))).thenReturn(partEntity);
        final CreatePartDto createPartDto = partTestUtils.generateMockCreateDto();
        final String payload = objectMapper.writeValueAsString(createPartDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Is.is(partEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(partEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(partEntity.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(partEntity.getStock())));
    }

    @Test
    void putWithValidPayloadReturnsUpdatedPartEntity() throws Exception {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        when(partService.update(any(String.class), any(UpdatePartDto.class))).thenReturn(partEntity);
        final UpdatePartDto updatePartDto = partTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updatePartDto);
        final String id = partEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/parts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(jsonPath("$.id", Is.is(partEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(partEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(partEntity.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(partEntity.getStock())));
    }

    @Test
    void deleteNonexistentPartReturnsEntityNotFoundException() throws Exception {
        when(partService.deleteById(any(String.class))).thenAnswer(i -> {
            throw new EntityNotFoundException(i.getArgument(0), PartEntity.class.getSimpleName());
        });
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/parts/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void deleteExistingPartReturnsDeletedPartEntity() throws Exception {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        when(partService.deleteById(any(String.class))).thenReturn(partEntity);
        final String id = partEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/parts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(partEntity.getId().toString())))
                .andExpect(jsonPath("$.name", Is.is(partEntity.getName())))
                .andExpect(jsonPath("$.price", Is.is(partEntity.getPrice())))
                .andExpect(jsonPath("$.stock", Is.is(partEntity.getStock())));
    }

}