package com.timo_noordzee.novi.backend.service;

import com.github.javafaker.Faker;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.dto.UpdateCustomerDto;
import com.timo_noordzee.novi.backend.exception.EmailTakenException;
import com.timo_noordzee.novi.backend.exception.EntityAlreadyExistsException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.mapper.CustomerMapper;
import com.timo_noordzee.novi.backend.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    private final Faker faker = new Faker(new Locale("nl"));

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    private CustomerEntity exampleCustomer;

    @BeforeEach
    void setup() {
        final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
        customerService = new CustomerService(customerRepository, customerMapper);
        exampleCustomer = CustomerEntity.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .createdAt(new Date())
                .build();
    }

    @Test
    void parsingInvalidIdThrowsInvalidUUIDException() {
        final InvalidUUIDException exception = assertThrows(InvalidUUIDException.class, () ->
                customerService.parseId("invalid-id"));

        assertThat(exception.getErrorCode()).isEqualTo(InvalidUUIDException.ERROR_CODE);
    }

    @Test
    void getAllOnEmptyDatabaseReturnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        final List<CustomerEntity> customerEntityList = customerService.getAll();

        assertThat(customerEntityList).isEmpty();
    }

    @Test
    void getByIdForNonexistentCustomerThrowsEntityNotFoundException() {
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                customerService.getById(id));

        assertThat(exception.getErrorCode()).isEqualTo(EntityNotFoundException.ERROR_CODE);
    }

    @Test
    void addAlreadyExistingCustomerThrowsEntityAlreadyExistsException() {
        when(customerRepository.existsById(any(UUID.class))).thenReturn(true);
        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();

        final EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () ->
                customerService.add(createCustomerDto));

        assertThat(exception.getErrorCode()).isEqualTo(EntityAlreadyExistsException.ERROR_CODE);
    }

    @Test
    void addWithTakenEmailThrowsEmailTakenException() {
        when(customerRepository.existsById(any(UUID.class))).thenReturn(false);
        when(customerRepository.existsByEmail(any(String.class))).thenReturn(true);
        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();

        final EmailTakenException exception = assertThrows(EmailTakenException.class, () ->
                customerService.add(createCustomerDto));

        assertThat(exception.getErrorCode()).isEqualTo(EmailTakenException.ERROR_CODE);
    }

    @Test
    void addValidNonexistentCustomerReturnsCustomer() {
        when(customerRepository.existsById(any(UUID.class))).thenReturn(false);
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(i -> {
            final CustomerEntity customerEntity = i.getArgument(0);
            customerEntity.setCreatedAt(new Date());
            return customerEntity;
        });
        final CreateCustomerDto createCustomerDto = CreateCustomerDto.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();

        final CustomerEntity customerEntity = customerService.add(createCustomerDto);

        assertThat(customerEntity).isNotNull();
        assertThat(customerEntity.getId()).isEqualTo(UUID.fromString(createCustomerDto.getId()));
        assertThat(customerEntity.getName()).isEqualTo(createCustomerDto.getName());
        assertThat(customerEntity.getSurname()).isEqualTo(createCustomerDto.getSurname());
        assertThat(customerEntity.getEmail()).isEqualTo(createCustomerDto.getEmail());
        assertThat(customerEntity.getPhone()).isEqualTo(createCustomerDto.getPhone());
        assertThat(customerEntity.getCreatedAt()).isBefore(new Date());
    }

    @Test
    void updateWithTakenEmailThrowsEmailTakenException() {
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";
        final UpdateCustomerDto updateCustomerDto = UpdateCustomerDto.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(exampleCustomer));
        when(customerRepository.existsByEmail(any(String.class))).thenReturn(true);

        final EmailTakenException exception = assertThrows(EmailTakenException.class, () ->
                customerService.update(id, updateCustomerDto));

        assertThat(exception.getErrorCode()).isEqualTo(EmailTakenException.ERROR_CODE);
    }

    @Test
    void updateReturnsUpdatedCustomer() {
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";
        final UpdateCustomerDto updateCustomerDto = UpdateCustomerDto.builder()
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(exampleCustomer));

        final CustomerEntity updatedCustomer = customerService.update(id, updateCustomerDto);

        assertThat(updatedCustomer.getName()).isEqualTo(updatedCustomer.getName());
        assertThat(updatedCustomer.getSurname()).isEqualTo(updatedCustomer.getSurname());
        assertThat(updatedCustomer.getEmail()).isEqualTo(updatedCustomer.getEmail());
        assertThat(updatedCustomer.getPhone()).isEqualTo(updatedCustomer.getPhone());
    }

    @Test
    void deleteReturnsDeletedCustomer() {
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(exampleCustomer));

        final CustomerEntity deletedCustomer = customerService.deleteById(id);

        assertThat(deletedCustomer).isEqualTo(exampleCustomer);
    }


}
