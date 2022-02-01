package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.exception.EntityAlreadyExistsException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.CustomerService;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
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

import java.util.UUID;

import static com.timo_noordzee.novi.backend.domain.Role.ROLE_ADMINISTRATIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@WithMockUser(roles = {ROLE_ADMINISTRATIVE})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerService customerService;

    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(customerService).isNotNull();
    }

    @Test
    void addCustomerWorksThroughAllLayers() throws Exception {
        final CreateCustomerDto createCustomerDto = customerTestUtils.generateMockCreateDto();
        final String payload = objectMapper.writeValueAsString(createCustomerDto);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Is.is(createCustomerDto.getName())))
                .andExpect(jsonPath("$.surname", Is.is(createCustomerDto.getSurname())))
                .andExpect(jsonPath("$.email", Is.is(createCustomerDto.getEmail())))
                .andExpect(jsonPath("$.phone", Is.is(createCustomerDto.getPhone())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andReturn();

        final String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
        assertThat(id).isNotEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Is.is(createCustomerDto.getName())))
                .andExpect(jsonPath("$.surname", Is.is(createCustomerDto.getSurname())))
                .andExpect(jsonPath("$.email", Is.is(createCustomerDto.getEmail())))
                .andExpect(jsonPath("$.phone", Is.is(createCustomerDto.getPhone())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void getNonExistingCustomerThrowsEntityNotFoundException() throws Exception {
        final String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void addingExistingCustomerThrowsEntityAlreadyExistsException() throws Exception {
        final CreateCustomerDto createCustomerDto = customerTestUtils.generateMockCreateDto();
        String payload = objectMapper.writeValueAsString(createCustomerDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        payload = objectMapper.writeValueAsString(createCustomerDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityAlreadyExistsException.ERROR_CODE)));
    }

    @Test
    void postWithInvalidPayloadReturnsValidationErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Is.is("field is required")))
                .andExpect(jsonPath("$.surname", Is.is("field is required")))
                .andExpect(jsonPath("$.email", Is.is("field is required")))
                .andExpect(jsonPath("$.phone", Is.is("field is required")));
    }

}

