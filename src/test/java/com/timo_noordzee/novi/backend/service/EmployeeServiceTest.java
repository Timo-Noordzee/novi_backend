package com.timo_noordzee.novi.backend.service;

import com.github.javafaker.Faker;
import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import com.timo_noordzee.novi.backend.domain.Role;
import com.timo_noordzee.novi.backend.dto.CreateEmployeeDto;
import com.timo_noordzee.novi.backend.dto.UpdateEmployeeDto;
import com.timo_noordzee.novi.backend.exception.EmailTakenException;
import com.timo_noordzee.novi.backend.exception.EntityAlreadyExistsException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.mapper.EmployeeMapper;
import com.timo_noordzee.novi.backend.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private final Faker faker = new Faker(new Locale("nl"));

    @Mock
    private EmployeeRepository employeeRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private EmployeeService employeeService;

    private EmployeeEntity exampleEmployee;

    @BeforeEach
    void setup() {
        final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);
        passwordEncoder = new BCryptPasswordEncoder();
        employeeService = new EmployeeService(employeeRepository, employeeMapper, passwordEncoder);

        exampleEmployee = EmployeeEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password("$2a$10$uPDc1UgeXiclJ84XYReOE.9ZlqlAwIT6MS6pyKiVMWnidZJf5ocQK")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void parsingInvalidIdThrowsInvalidUUIDException() {
        final InvalidUUIDException exception = assertThrows(InvalidUUIDException.class, () ->
                employeeService.parseId("invalid-id"));

        assertThat(exception.getErrorCode()).isEqualTo(InvalidUUIDException.ERROR_CODE);
    }

    @Test
    void getAllOnEmptyDatabaseReturnsEmptyList() {
        when(employeeRepository.findAll()).thenReturn(new ArrayList<>());

        final List<EmployeeEntity> employeeEntityList = employeeService.getAll();

        assertThat(employeeEntityList).isEmpty();
    }

    @Test
    void getByIdForNonexistentEmployeeThrowsEntityNotFoundException() {
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                employeeService.getById(id));

        assertThat(exception.getErrorCode()).isEqualTo(EntityNotFoundException.ERROR_CODE);
    }

    @Test
    void addAlreadyExistingEmployeeThrowsEntityAlreadyExistsException() {
        when(employeeRepository.existsById(any(UUID.class))).thenReturn(true);
        final CreateEmployeeDto createEmployeeDto = CreateEmployeeDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .role("admin")
                .build();

        final EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () ->
                employeeService.add(createEmployeeDto));

        assertThat(exception.getErrorCode()).isEqualTo(EntityAlreadyExistsException.ERROR_CODE);
    }

    @Test
    void addWithTakenEmailThrowsEmailTakenException() {
        when(employeeRepository.existsById(any(UUID.class))).thenReturn(false);
        when(employeeRepository.existsByEmail(any(String.class))).thenReturn(true);
        final CreateEmployeeDto createEmployeeDto = CreateEmployeeDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .role("admin")
                .build();

        final EmailTakenException exception = assertThrows(EmailTakenException.class, () ->
                employeeService.add(createEmployeeDto));

        assertThat(exception.getErrorCode()).isEqualTo(EmailTakenException.ERROR_CODE);
    }

    @Test
    void addValidNonexistentEmployeeReturnsEmployee() {
        when(employeeRepository.existsById(any(UUID.class))).thenReturn(false);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenAnswer(i -> i.getArgument(0));
        final CreateEmployeeDto createEmployeeDto = CreateEmployeeDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .role("admin")
                .build();

        final EmployeeEntity employeeEntity = employeeService.add(createEmployeeDto);

        assertThat(employeeEntity).isNotNull();
        assertThat(employeeEntity.getId()).isEqualTo(UUID.fromString(createEmployeeDto.getId()));
        assertThat(employeeEntity.getName()).isEqualTo(createEmployeeDto.getName());
        assertThat(employeeEntity.getSurname()).isEqualTo(createEmployeeDto.getSurname());
        assertThat(employeeEntity.getEmail()).isEqualTo(createEmployeeDto.getEmail());
        assertThat(employeeEntity.getRole()).isEqualTo(Role.ADMIN);
        assertThat(passwordEncoder.matches(createEmployeeDto.getPassword(), employeeEntity.getPassword())).isTrue();
    }

    @Test
    void updateReturnsUpdatedEmployee() {
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";
        final String password = faker.internet().password();
        final UpdateEmployeeDto updateEmployeeDto = UpdateEmployeeDto.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .role("cashier")
                .password(password)
                .build();
        when(employeeRepository.save(any(EmployeeEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(exampleEmployee));

        final EmployeeEntity updatedEmployee = employeeService.update(id, updateEmployeeDto);

        assertThat(updatedEmployee.getName()).isEqualTo(updatedEmployee.getName());
        assertThat(updatedEmployee.getSurname()).isEqualTo(updatedEmployee.getSurname());
        assertThat(updatedEmployee.getRole()).isEqualTo(Role.CASHIER);
        assertThat(passwordEncoder.matches(password, updatedEmployee.getPassword())).isTrue();
    }

    @Test
    void deleteReturnsDeletedEmployee() {
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(exampleEmployee));

        final EmployeeEntity deletedEmployee = employeeService.deleteById(id);

        assertThat(deletedEmployee).isEqualTo(exampleEmployee);
    }

}
