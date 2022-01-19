package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.jayway.jsonpath.JsonPath;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.exception.EntityAlreadyExistsException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.CustomerService;
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

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomerIntegrationTest {

    private final Faker faker = new Faker(new Locale("nl"));

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerService customerService;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(customerService).isNotNull();
    }

    @Test
    void addCustomerWorksThroughAllLayers() throws Exception {
        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build();

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCustomerDto)))
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
        final String id = UUID.randomUUID().toString();

        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .id(id)
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCustomerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCustomerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityAlreadyExistsException.ERROR_CODE)));
    }

    @Test
    void postWithInvalidPayloadReturnsValidationErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Is.is("field is required")))
                .andExpect(jsonPath("$.surname", Is.is("field is required")))
                .andExpect(jsonPath("$.email", Is.is("field is required")))
                .andExpect(jsonPath("$.phone", Is.is("field is required")));
    }

}

