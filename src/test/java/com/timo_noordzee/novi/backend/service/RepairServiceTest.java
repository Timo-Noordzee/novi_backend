package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.mapper.*;
import com.timo_noordzee.novi.backend.repository.*;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.RepairTestUtils;
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
public class RepairServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RepairRepository repairRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private RepairLineRepository repairLineRepository;

    private RepairService repairService;

    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final RepairTestUtils repairTestUtils = new RepairTestUtils();

    @BeforeEach
    void setUp() {
        final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
        final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);
        final PartMapper partMapper = Mappers.getMapper(PartMapper.class);
        final ActionMapper actionMapper = Mappers.getMapper(ActionMapper.class);
        final RepairMapper repairMapper = Mappers.getMapper(RepairMapper.class);
        final RepairLineMapper repairLineMapper = Mappers.getMapper(RepairLineMapper.class);
        final CustomerService customerService = new CustomerService(customerRepository, customerMapper);
        final VehicleService vehicleService = new VehicleService(vehicleRepository, vehicleMapper, customerService);
        final PartService partService = new PartService(partRepository, partMapper);
        final ActionService actionService = new ActionService(actionRepository, actionMapper);
        repairService = new RepairService(repairRepository, repairMapper, vehicleService, partService, actionService, repairLineRepository, repairLineMapper);
    }

    @Test
    void parsingInvalidIdThrowsInvalidUUIDException() {
        final String invalidUUID = "invalid-id";

        assertThrows(InvalidUUIDException.class, () -> repairService.parseId(invalidUUID));
    }

    @Test
    void getByIdForNonexistentRepairThrowsEntityNotFoundException() {
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.getById(id));
    }

    @Test
    void addRepairToNonexistentVehicleThrowsVehicleNotFoundException() {
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String vehicleId = vehicleTestUtils.randomVin();
        final CreateRepairDto createRepairDto = repairTestUtils.generateMockCreateDto(vehicleId);

        assertThrows(EntityNotFoundException.class, () -> repairService.add(createRepairDto));
    }

    @Test
    void addingValidNonexistentRepairToVehicleReturnsRepairEntity() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        when(repairRepository.save(any(RepairEntity.class))).thenAnswer(i -> {
            final RepairEntity repairEntity = i.getArgument(0);
            repairEntity.setCreatedAt(new Date());
            return repairEntity;
        });
        final CreateRepairDto createRepairDto = repairTestUtils.generateMockCreateDto(vehicleEntity.getVin());

        final RepairEntity repairEntity = repairService.add(createRepairDto);

        assertThat(repairEntity).isNotNull();
        assertThat(repairEntity.getCreatedAt()).isNotNull();
        assertThat(repairEntity.getRemarks()).isEqualTo(createRepairDto.getRemarks());
        assertThat(repairEntity.getStatus().getValue()).isEqualTo(createRepairDto.getStatus());
        assertThat(repairEntity.getVehicle().getOwner()).isEqualTo(vehicleEntity.getOwner());
    }

    @Test
    void updatingNonexistentRepairThrowsEntityNotFoundException() {
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final UpdateRepairDto updateRepairDto = repairTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.update(id, updateRepairDto));
    }

    @Test
    void updateExistingRepairUpdatesRepairEntity() {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.of(repairEntity));
        when(repairRepository.save(any(RepairEntity.class))).thenAnswer(i -> i.getArgument(0));
        final UpdateRepairDto updateRepairDto = repairTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        final RepairEntity updatedShortcomingEntity = repairService.update(id, updateRepairDto);

        assertThat(updatedShortcomingEntity).isNotNull();
        assertThat(updatedShortcomingEntity.getRemarks()).isEqualTo(updatedShortcomingEntity.getRemarks());
        assertThat(updatedShortcomingEntity.getVehicle()).isEqualTo(vehicleEntity);
    }

    @Test
    void deletingNonexistentShortcomingThrowsEntityNotFoundException() {
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.deleteById(id));
    }

    @Test
    void deletingExistentShortcomingThrowsEntityReturnsShortcomingEntity() {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.of(repairEntity));

        final RepairEntity deletedRepairEntity = repairService.deleteById(repairEntity.getId().toString());

        assertThat(deletedRepairEntity).isNotNull();
        assertThat(deletedRepairEntity.getId()).isEqualTo(repairEntity.getId());
        assertThat(deletedRepairEntity.getVehicle()).isEqualTo(repairEntity.getVehicle());
    }

}
