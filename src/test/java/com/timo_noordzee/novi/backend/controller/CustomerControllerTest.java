package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.dto.UpdateCustomerDto;
import com.timo_noordzee.novi.backend.exception.EmailTakenException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.CustomerService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private final Faker faker = new Faker(new Locale("nl"));

    private CustomerEntity firstCustomer;
    private CustomerEntity secondCustomer;

    @BeforeEach
    void setup() {
        firstCustomer = CustomerEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .createdAt(faker.date().past(1, TimeUnit.DAYS))
                .build();

        secondCustomer = CustomerEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .createdAt(faker.date().past(1, TimeUnit.DAYS))
                .build();
    }

    @Test
    void getAllReturnsArrayOfCustomers() throws Exception {
        final List<CustomerEntity> customerEntityList = new ArrayList<>();
        customerEntityList.add(firstCustomer);
        customerEntityList.add(secondCustomer);
        when(customerService.getAll()).thenReturn(customerEntityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(firstCustomer.getId().toString())))
                .andExpect(jsonPath("$[0].phone", Is.is(firstCustomer.getPhone())))
                .andExpect(jsonPath("$[1].id", Is.is(secondCustomer.getId().toString())))
                .andExpect(jsonPath("$[1].phone", Is.is(secondCustomer.getPhone())));
    }

    @Test
    void getByIdOnNonexistentCustomerReturnsEntityNotFoundException() throws Exception {
        final String id = "be3ca880-8c7b-44d3-9ec2-1c16d1c041a8";
        when(customerService.getById(any(String.class)))
                .thenThrow(new EntityNotFoundException(id, CustomerEntity.class.getSimpleName()));

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void addingWithInvalidPayloadReturnsValidationErrors() throws Exception {
        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .email("invalid-email")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .content(objectMapper.writeValueAsString(createCustomerDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", Is.is("field is required")))
                .andExpect(jsonPath("$.surname", Is.is("field is required")))
                .andExpect(jsonPath("$.email", Is.is("isn't a valid email address")))
                .andExpect(jsonPath("$.phone", Is.is("field is required")));
    }

    @Test
    void addingWithTakenEmailReturnsEmailTakenException() throws Exception {
        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .id(firstCustomer.getId().toString())
                .name(firstCustomer.getName())
                .surname(firstCustomer.getSurname())
                .email(firstCustomer.getEmail())
                .phone(firstCustomer.getPhone())
                .build();
        when(customerService.add(any(CreateCustomerDto.class)))
                .thenThrow(new EmailTakenException(createCustomerDto.getEmail()));

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCustomerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(EmailTakenException.ERROR_CODE)));
    }

    @Test
    void addingWithValidPayloadReturnsCustomerEntity() throws Exception {
        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .id(firstCustomer.getId().toString())
                .name(firstCustomer.getName())
                .surname(firstCustomer.getSurname())
                .email(firstCustomer.getEmail())
                .phone(firstCustomer.getPhone())
                .build();
        when(customerService.add(any(CreateCustomerDto.class))).thenReturn(firstCustomer);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createCustomerDto)));

        resultActions.andExpect(status().isCreated());
        assertResultMatchesEntity(resultActions, firstCustomer);
    }

    @Test
    void updatingWithValidPayloadReturnsUpdatedCustomer() throws Exception {
        final String id = "be3ca880-8c7b-44d3-9ec2-1c16d1c041a8";
        final UpdateCustomerDto updateCustomerDto = UpdateCustomerDto.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.internet().password())
                .build();
        when(customerService.update(any(String.class), any(UpdateCustomerDto.class))).thenReturn(secondCustomer);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/customers/{id}", id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateCustomerDto)));

        resultActions.andExpect(status().isOk());
        assertResultMatchesEntity(resultActions, secondCustomer);
    }

    @Test
    void deletingExistingCustomerReturnsCustomer() throws Exception {
        final String id = "be3ca880-8c7b-44d3-9ec2-1c16d1c041a8";
        when(customerService.deleteById(any(String.class))).thenReturn(firstCustomer);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/customers/{id}", id));

        resultActions.andExpect(status().isOk());
        assertResultMatchesEntity(resultActions, firstCustomer);
    }

    private void assertResultMatchesEntity(
            final ResultActions resultActions,
            final CustomerEntity customerEntity
    ) throws Exception {
        resultActions.andExpect(jsonPath("$.id", Is.is(customerEntity.getId().toString())));
        resultActions.andExpect(jsonPath("$.name", Is.is(customerEntity.getName())));
        resultActions.andExpect(jsonPath("$.surname", Is.is(customerEntity.getSurname())));
        resultActions.andExpect(jsonPath("$.email", Is.is(customerEntity.getEmail())));
        resultActions.andExpect(jsonPath("$.phone", Is.is(customerEntity.getPhone())));
    }
}