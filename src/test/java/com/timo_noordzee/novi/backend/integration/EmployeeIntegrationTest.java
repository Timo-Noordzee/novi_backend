package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.domain.Role;
import com.timo_noordzee.novi.backend.dto.CreateEmployeeDto;
import com.timo_noordzee.novi.backend.exception.EntityAlreadyExistsException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.util.EmployeeTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static com.timo_noordzee.novi.backend.domain.Role.ROLE_ADMIN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@WithMockUser(roles = {ROLE_ADMIN})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EmployeeTestUtils employeeTestUtils = new EmployeeTestUtils();

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
    }

    @Test
    void addEmployeeWorksThroughAllLayers() throws Exception {
        final Role role = employeeTestUtils.randomRole();
        final CreateEmployeeDto createEmployeeDto = employeeTestUtils.generateMockCreateDto(role);
        final String payload = objectMapper.writeValueAsString(createEmployeeDto);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Is.is(createEmployeeDto.getName())))
                .andExpect(jsonPath("$.surname", Is.is(createEmployeeDto.getSurname())))
                .andExpect(jsonPath("$.email", Is.is(createEmployeeDto.getEmail())))
                .andExpect(jsonPath("$.role", Is.is(role.getValue())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        final String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
        assertThat(id).isNotEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Is.is(createEmployeeDto.getName())))
                .andExpect(jsonPath("$.surname", Is.is(createEmployeeDto.getSurname())))
                .andExpect(jsonPath("$.email", Is.is(createEmployeeDto.getEmail())))
                .andExpect(jsonPath("$.role", Is.is(role.getValue())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getNonExistingEmployeeThrowsEntityNotFoundException() throws Exception {
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/employees/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void addingExistingEmployeeThrowsEntityAlreadyExistsException() throws Exception {
        final CreateEmployeeDto createEmployeeDto = employeeTestUtils.generateMockCreateDto();
        String payload = objectMapper.writeValueAsString(createEmployeeDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        payload = objectMapper.writeValueAsString(createEmployeeDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityAlreadyExistsException.ERROR_CODE)));
    }

    @Test
    void postWithInvalidPayloadReturnsValidationErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Is.is("field is required")))
                .andExpect(jsonPath("$.surname", Is.is("field is required")))
                .andExpect(jsonPath("$.email", Is.is("field is required")))
                .andExpect(jsonPath("$.role", Is.is("field is required")))
                .andExpect(jsonPath("$.password", Is.is("field is required")));
    }

}
