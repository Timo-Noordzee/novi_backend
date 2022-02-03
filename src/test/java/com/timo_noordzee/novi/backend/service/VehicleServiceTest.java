package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import com.timo_noordzee.novi.backend.exception.EntityAlreadyExistsException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.LicenseTakenException;
import com.timo_noordzee.novi.backend.mapper.CustomerMapper;
import com.timo_noordzee.novi.backend.mapper.VehicleMapper;
import com.timo_noordzee.novi.backend.repository.CustomerRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    private VehicleService vehicleService;


    @BeforeEach
    void setup() {
        final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
        final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);
        final CustomerService customerService = new CustomerService(customerRepository, customerMapper);
        vehicleService = new VehicleService(vehicleRepository, vehicleMapper, customerService);
    }

    @Test
    void parsingIdDoesNotModifyValue() {
        final String vin = vehicleTestUtils.randomVin();

        final String parsed = vehicleService.parseId(vin);

        assertThat(parsed).isEqualTo(vin);
    }

    @Test
    void getAllOnEmptyDatabaseReturnsEmptyList() {
        when(vehicleService.findAll()).thenReturn(new ArrayList<>());

        final List<VehicleEntity> customerEntityList = vehicleService.getAll();

        assertThat(customerEntityList).isEmpty();
    }

    @Test
    void getByIdForNonexistentVehicleThrowsEntityNotFoundException() {
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String id = "d145f0fa-f261-41d7-8c0d-f0bc43ffd805";

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vehicleService.getById(id));

        assertThat(exception.getErrorCode()).isEqualTo(EntityNotFoundException.ERROR_CODE);
    }

    @Test
    void addingAlreadyExistingVehicleThrowsEntityAlreadyExistsException() {
        when(vehicleRepository.existsById(any(String.class))).thenReturn(true);
        final String customerId = vehicleTestUtils.randomCustomerId();
        final CreateVehicleDto createVehicleDto = vehicleTestUtils.generateMockCreateDto(customerId);

        final EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () ->
                vehicleService.add(createVehicleDto));

        assertThat(exception.getErrorCode()).isEqualTo(EntityAlreadyExistsException.ERROR_CODE);
    }

    @Test
    void addVehicleWithTakenLicenseThrowsLicenseTakenException() {
        when(vehicleRepository.existsById(any(String.class))).thenReturn(false);
        when(vehicleRepository.existsByLicense(any(String.class))).thenReturn(true);
        final String customerId = vehicleTestUtils.randomCustomerId();
        final CreateVehicleDto createVehicleDto = vehicleTestUtils.generateMockCreateDto(customerId);

        final LicenseTakenException exception = assertThrows(LicenseTakenException.class, () ->
                vehicleService.add(createVehicleDto));

        assertThat(exception.getErrorCode()).isEqualTo(LicenseTakenException.ERROR_CODE);
    }

    @Test
    void addVehicleWithNonExistentCustomerIdThrowsEntityNotFoundException() {
        when(vehicleRepository.existsById(any(String.class))).thenReturn(false);
        when(vehicleRepository.existsByLicense(any(String.class))).thenReturn(false);
        when(customerRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String customerId = vehicleTestUtils.randomCustomerId();
        final CreateVehicleDto createVehicleDto = vehicleTestUtils.generateMockCreateDto(customerId);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vehicleService.add(createVehicleDto));

        assertThat(exception.getErrorCode()).isEqualTo(EntityNotFoundException.ERROR_CODE);
    }

    @Test
    void addValidNonExistentVehicleReturnsVehicleWithCustomer() {
        when(vehicleRepository.existsById(any(String.class))).thenReturn(false);
        when(vehicleRepository.existsByLicense(any(String.class))).thenReturn(false);
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        when(customerRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.of(customerEntity));
        final CreateVehicleDto createVehicleDto = vehicleTestUtils.generateMockCreateDto(customerEntity.getId());
        when(vehicleRepository.save(any(VehicleEntity.class))).thenAnswer(i -> {
            final VehicleEntity vehicleEntity = i.getArgument(0);
            vehicleEntity.setCreatedAt(new Date());
            return vehicleEntity;
        });

        final VehicleEntity vehicleEntity = vehicleService.add(createVehicleDto);

        assertThat(vehicleEntity).isNotNull();
        assertThat(vehicleEntity.getVin()).isEqualTo(createVehicleDto.getVin());
        assertThat(vehicleEntity.getLicense()).isEqualTo(createVehicleDto.getLicense());
        assertThat(vehicleEntity.getBrand()).isEqualTo(createVehicleDto.getBrand());
        assertThat(vehicleEntity.getMake()).isEqualTo(createVehicleDto.getMake());
        assertThat(vehicleEntity.getYear()).isEqualTo(createVehicleDto.getYear());
        assertThat(vehicleEntity.getOwner()).isEqualTo(customerEntity);
    }

    @Test
    void updateNotExistentVehicleTrowsEntityNotFoundException() {
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final UpdateVehicleDto updateVehicleDto = vehicleTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vehicleService.update(id, updateVehicleDto));

        assertThat(exception.getErrorCode()).isEqualTo(EntityNotFoundException.ERROR_CODE);
    }

    @Test
    void updateVehicleWithTakenLicenseThrowsLicenseTakenException() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.existsByLicense(any(String.class))).thenReturn(true);
        final UpdateVehicleDto updateVehicleDto = vehicleTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        final LicenseTakenException exception = assertThrows(LicenseTakenException.class, () ->
                vehicleService.update(id, updateVehicleDto));

        assertThat(exception.getErrorCode()).isEqualTo(LicenseTakenException.ERROR_CODE);
    }

    @Test
    void updateVehicleWithNonExistentCustomerThrowsEntityNotFoundException() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.existsByLicense(any(String.class))).thenReturn(false);
        when(customerRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String customerId = vehicleTestUtils.randomCustomerId();
        final UpdateVehicleDto updateVehicleDto = vehicleTestUtils.generateMockUpdateDto(customerId);
        final String id = UUID.randomUUID().toString();

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vehicleService.update(id, updateVehicleDto));

        assertThat(exception.getErrorCode()).isEqualTo(EntityNotFoundException.ERROR_CODE);
    }

    @Test
    void updateVehicleWithoutCustomerIdFieldDoesNotModifyCustomer() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.existsByLicense(any(String.class))).thenReturn(false);
        when(vehicleRepository.save(any(VehicleEntity.class))).thenAnswer(i -> i.getArgument(0));
        final UpdateVehicleDto updateVehicleDto = vehicleTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        final VehicleEntity updatedVehicle = vehicleService.update(id, updateVehicleDto);

        assertThat(updatedVehicle).isNotNull();
        assertThat(updatedVehicle.getOwner()).isEqualTo(customerEntity);
    }

    @Test
    void updateVehicleWithExistingCustomerWorks() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        final CustomerEntity newCustomerEntity = customerTestUtils.generateMockEntity();
        when(customerRepository.findById(eq(newCustomerEntity.getId()), any(EntityGraph.class))).thenReturn(Optional.of(newCustomerEntity));
        final UpdateVehicleDto updateVehicleDto = UpdateVehicleDto.builder()
                .customerId(newCustomerEntity.getId().toString())
                .build();
        final String id = UUID.randomUUID().toString();
        when(vehicleRepository.save(any(VehicleEntity.class))).thenAnswer(i -> i.getArgument(0));

        final VehicleEntity updatedVehicle = vehicleService.update(id, updateVehicleDto);

        assertThat(updatedVehicle).isNotNull();
        assertThat(updatedVehicle.getOwner()).isEqualTo(newCustomerEntity);
    }

}
