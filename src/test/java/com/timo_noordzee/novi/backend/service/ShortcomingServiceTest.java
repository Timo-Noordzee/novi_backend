package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.mapper.CustomerMapper;
import com.timo_noordzee.novi.backend.mapper.ShortcomingMapper;
import com.timo_noordzee.novi.backend.mapper.VehicleMapper;
import com.timo_noordzee.novi.backend.repository.CustomerRepository;
import com.timo_noordzee.novi.backend.repository.ShortcomingRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.ShortcomingTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShortcomingServiceTest {

    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final ShortcomingTestUtils shortcomingTestUtils = new ShortcomingTestUtils();

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ShortcomingRepository shortcomingRepository;

    private ShortcomingService shortcomingService;

    @BeforeEach
    void setUp() {
        final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);
        final ShortcomingMapper shortcomingMapper = Mappers.getMapper(ShortcomingMapper.class);
        final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
        final CustomerService customerService = new CustomerService(customerRepository, customerMapper);
        final VehicleService vehicleService = new VehicleService(vehicleRepository, vehicleMapper, customerService);
        shortcomingService = new ShortcomingService(shortcomingRepository, shortcomingMapper, vehicleService);
    }

    @Test
    void parsingInvalidIdThrowsInvalidUUIDException() {
        final String invalidUUID = "invalid-id";

        assertThrows(InvalidUUIDException.class, () -> shortcomingService.parseId(invalidUUID));
    }

    @Test
    void getByIdForNonexistentShortcomingThrowsEntityNotFoundException() {
        when(shortcomingRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> shortcomingService.getById(id));
    }

    @Test
    void addingShortcomingToNonexistentVehicleThrowsEntityNotFoundException() {
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String vehicleId = vehicleTestUtils.randomVin();
        final CreateShortcomingDto createShortcomingDto = shortcomingTestUtils.generateMockCreateDto(vehicleId);

        assertThrows(EntityNotFoundException.class, () -> shortcomingService.add(createShortcomingDto));
    }

    @Test
    void addingValidNonexistentShortcomingToVehicleReturnsShortcomingEntity() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        when(shortcomingRepository.save(any(ShortcomingEntity.class))).thenAnswer(i -> {
            final ShortcomingEntity shortcomingEntity = i.getArgument(0);
            shortcomingEntity.setCreatedAt(new Date());
            return shortcomingEntity;
        });
        final CreateShortcomingDto createShortcomingDto = shortcomingTestUtils.generateMockCreateDto(vehicleEntity.getVin());

        final ShortcomingEntity shortcomingEntity = shortcomingService.add(createShortcomingDto);

        assertThat(shortcomingEntity).isNotNull();
        assertThat(shortcomingEntity.getCreatedAt()).isNotNull();
        assertThat(shortcomingEntity.getDescription()).isEqualTo(createShortcomingDto.getDescription());
        assertThat(shortcomingEntity.getVehicle()).isEqualTo(vehicleEntity);
        assertThat(shortcomingEntity.getVehicle().getOwner()).isEqualTo(vehicleEntity.getOwner());
    }

    @Test
    void updatingNonexistentShortcomingThrowsEntityNotFoundException() {
        when(shortcomingRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final UpdateShortcomingDto updateShortcomingDto = shortcomingTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> shortcomingService.update(id, updateShortcomingDto));
    }

    @Test
    void updateExistingShortcomingUpdatesShortcomingEntity() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        when(shortcomingRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.of(shortcomingEntity));
        when(shortcomingRepository.save(any(ShortcomingEntity.class))).thenAnswer(i -> i.getArgument(0));
        final UpdateShortcomingDto updateShortcomingDto = shortcomingTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        final ShortcomingEntity updatedShortcomingEntity = shortcomingService.update(id, updateShortcomingDto);

        assertThat(updatedShortcomingEntity).isNotNull();
        assertThat(updatedShortcomingEntity.getDescription()).isEqualTo(updatedShortcomingEntity.getDescription());
        assertThat(updatedShortcomingEntity.getVehicle()).isEqualTo(vehicleEntity);
    }

    @Test
    void deletingNonexistentShortcomingThrowsEntityNotFoundException() {
        when(shortcomingRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> shortcomingService.deleteById(id));
    }

    @Test
    void deletingExistentShortcomingThrowsEntityReturnsShortcomingEntity() {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final ShortcomingEntity shortcomingEntity = shortcomingTestUtils.generateMockEntity(vehicleEntity);
        when(shortcomingRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.of(shortcomingEntity));

        final ShortcomingEntity deletedShortcomingEntity = shortcomingService.deleteById(shortcomingEntity.getId().toString());

        assertThat(deletedShortcomingEntity).isNotNull();
        assertThat(deletedShortcomingEntity.getId()).isEqualTo(shortcomingEntity.getId());
        assertThat(deletedShortcomingEntity.getVehicle()).isEqualTo(shortcomingEntity.getVehicle());
    }


}
