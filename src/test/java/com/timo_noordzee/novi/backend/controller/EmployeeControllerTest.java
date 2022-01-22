package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import com.timo_noordzee.novi.backend.dto.CreateEmployeeDto;
import com.timo_noordzee.novi.backend.dto.UpdateEmployeeDto;
import com.timo_noordzee.novi.backend.exception.EmailTakenException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.service.EmployeeService;
import com.timo_noordzee.novi.backend.util.EmployeeTestUtils;
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
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private final EmployeeTestUtils employeeTestUtils = new EmployeeTestUtils();

    private EmployeeEntity firstEmployee;
    private EmployeeEntity secondEmployee;

    @BeforeEach
    void setup() {
        firstEmployee = employeeTestUtils.generateMockEntity();
        secondEmployee = employeeTestUtils.generateMockEntity();
    }

    @Test
    void getAllReturnsArrayOfEmployees() throws Exception {
        final List<EmployeeEntity> employeeEntityList = new ArrayList<>();
        employeeEntityList.add(firstEmployee);
        employeeEntityList.add(secondEmployee);
        when(employeeService.getAll()).thenReturn(employeeEntityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(firstEmployee.getId().toString())))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].role", Is.is(firstEmployee.getRole().getValue())))
                .andExpect(jsonPath("$[1].id", Is.is(secondEmployee.getId().toString())))
                .andExpect(jsonPath("$[1].password").doesNotExist())
                .andExpect(jsonPath("$[1].role", Is.is(secondEmployee.getRole().getValue())));
    }

    @Test
    void getByIdOnNonexistentEmployeeReturnsEntityNotFoundException() throws Exception {
        final String id = "be3ca880-8c7b-44d3-9ec2-1c16d1c041a8";
        when(employeeService.getById(any(String.class)))
                .thenThrow(new EntityNotFoundException(id, EmployeeEntity.class.getSimpleName()));

        mockMvc.perform(MockMvcRequestBuilders.get("/employees/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void addingWithInvalidPayloadReturnsValidationErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .content("{}")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", Is.is("field is required")))
                .andExpect(jsonPath("$.surname", Is.is("field is required")))
                .andExpect(jsonPath("$.email", Is.is("field is required")))
                .andExpect(jsonPath("$.role", Is.is("field is required")))
                .andExpect(jsonPath("$.password", Is.is("field is required")));
    }

    @Test
    void addingWithTakenEmailReturnsEmailTakenException() throws Exception {
        final CreateEmployeeDto createEmployeeDto = CreateEmployeeDto.builder()
                .id(firstEmployee.getId().toString())
                .name(firstEmployee.getName())
                .surname(firstEmployee.getSurname())
                .email(firstEmployee.getEmail())
                .password("123456")
                .role("admin")
                .build();
        when(employeeService.add(any(CreateEmployeeDto.class)))
                .thenThrow(new EmailTakenException(createEmployeeDto.getEmail()));

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createEmployeeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(EmailTakenException.ERROR_CODE)));
    }

    @Test
    void addingWithValidPayloadReturnsEmployeeEntity() throws Exception {
        final EmployeeEntity employeeEntity = employeeTestUtils.generateMockEntity();
        final CreateEmployeeDto createEmployeeDto = employeeTestUtils.generateMockCreateDto();
        createEmployeeDto.setId(employeeEntity.getId().toString());
        when(employeeService.add(any(CreateEmployeeDto.class))).thenReturn(employeeEntity);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/employees")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createEmployeeDto)));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.id", Is.is(employeeEntity.getId().toString())));
        resultActions.andExpect(jsonPath("$.name", Is.is(employeeEntity.getName())));
        resultActions.andExpect(jsonPath("$.surname", Is.is(employeeEntity.getSurname())));
        resultActions.andExpect(jsonPath("$.email", Is.is(employeeEntity.getEmail())));
        resultActions.andExpect(jsonPath("$.role", Is.is(employeeEntity.getRole().toString())));
        resultActions.andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void updatingWithValidPayloadReturnsUpdatedEmployee() throws Exception {
        final String id = UUID.randomUUID().toString();
        final UpdateEmployeeDto updateEmployeeDto = employeeTestUtils.generateMockUpdateDto();
        when(employeeService.update(any(String.class), any(UpdateEmployeeDto.class))).thenReturn(secondEmployee);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/employees/{id}", id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateEmployeeDto)));

        resultActions.andExpect(status().isOk());
        assertResultMatchesEntity(resultActions, secondEmployee);
    }

    @Test
    void deletingExistingEmployeeReturnsEmployee() throws Exception {
        final String id = UUID.randomUUID().toString();
        when(employeeService.deleteById(any(String.class))).thenReturn(firstEmployee);

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/employees/{id}", id));

        resultActions.andExpect(status().isOk());
        assertResultMatchesEntity(resultActions, firstEmployee);
    }

    private void assertResultMatchesEntity(
            final ResultActions resultActions,
            final EmployeeEntity employeeEntity
    ) throws Exception {
        resultActions.andExpect(jsonPath("$.id", Is.is(employeeEntity.getId().toString())));
        resultActions.andExpect(jsonPath("$.name", Is.is(employeeEntity.getName())));
        resultActions.andExpect(jsonPath("$.surname", Is.is(employeeEntity.getSurname())));
        resultActions.andExpect(jsonPath("$.email", Is.is(employeeEntity.getEmail())));
        resultActions.andExpect(jsonPath("$.role", Is.is(employeeEntity.getRole().getValue())));
        resultActions.andExpect(jsonPath("$.password").doesNotExist());
    }
}
