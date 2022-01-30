package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.timo_noordzee.novi.backend.data.*;
import com.timo_noordzee.novi.backend.dto.AddRepairLinesDto;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairLineDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.exception.OutOfStockException;
import com.timo_noordzee.novi.backend.exception.UnknownRepairLineTypeException;
import com.timo_noordzee.novi.backend.mapper.*;
import com.timo_noordzee.novi.backend.repository.*;
import com.timo_noordzee.novi.backend.util.*;
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
import static org.mockito.ArgumentMatchers.eq;
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
    private final RepairLineTestUtils repairLineTestUtils = new RepairLineTestUtils();
    private final PartTestUtils partTestUtils = new PartTestUtils();

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

    RepairEntity setupMockRepair() {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        when(repairRepository.findById(any(UUID.class) ,any(EntityGraph.class))).thenReturn(Optional.of(repairEntity));
        return repairEntity;
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

    @Test
    void addPartLineWithNonexistentPartIdThrowsEntityNotFoundException() {
        final RepairEntity repairEntity = setupMockRepair();
        when(partRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final AddRepairLinesDto.Part addPartLineDto = repairLineTestUtils.generateMockPartLineDto(UUID.randomUUID().toString(), 1);
        final AddRepairLinesDto addRepairLinesDto = repairLineTestUtils.generateMockDto(addPartLineDto);
        final String repairId = repairEntity.getId().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.addLinesToRepair(repairId, addRepairLinesDto));
    }

    @Test
    void addPartLineWithoutStockThrowsOutOfStockException() {
        final RepairEntity repairEntity = setupMockRepair();
        final PartEntity partEntity = partTestUtils.generateMockEntity(0);
        when(partRepository.findById(eq(partEntity.getId()))).thenReturn(Optional.of(partEntity));
        final AddRepairLinesDto.Part addPartLineDto = repairLineTestUtils.generateMockPartLineDto(partEntity, 1);
        final AddRepairLinesDto addRepairLinesDto = repairLineTestUtils.generateMockDto(addPartLineDto);
        final String repairId = repairEntity.getId().toString();

        assertThrows(OutOfStockException.class, () -> repairService.addLinesToRepair(repairId, addRepairLinesDto));
    }

    @Test
    void addActionLineWithNonexistentActionIdThrowsEntityNotFoundException() {
        final RepairEntity repairEntity = setupMockRepair();
        when(actionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final AddRepairLinesDto.Action addActionLineDto = repairLineTestUtils.generateMockActionLineDto(UUID.randomUUID().toString(), 1);
        final AddRepairLinesDto addRepairLinesDto = repairLineTestUtils.generateMockDto(addActionLineDto);
        final String repairId = repairEntity.getId().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.addLinesToRepair(repairId, addRepairLinesDto));
    }

    @Test
    void addCustomLineWithUnknownRepairLineTypeThrowsUnknownRepairLineTypeException() {
        final RepairEntity repairEntity = setupMockRepair();
        final AddRepairLinesDto.Custom addCustomLineDto = repairLineTestUtils.generateMockCustomLineDto(3);
        final AddRepairLinesDto addRepairLinesDto = repairLineTestUtils.generateMockDto(addCustomLineDto);
        final String repairId = repairEntity.getId().toString();

        assertThrows(UnknownRepairLineTypeException.class, () -> repairService.addLinesToRepair(repairId, addRepairLinesDto));
    }

    @Test
    void updateRepairLineForUnknownRepairThrowsEntityNotFoundException() {
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String repairId = UUID.randomUUID().toString();
        final String repairLineId = UUID.randomUUID().toString();
        final UpdateRepairLineDto updateRepairLineDto = repairLineTestUtils.generateMockUpdateDto();

        assertThrows(EntityNotFoundException.class, () -> repairService.updateRepairLine(repairId, repairLineId, updateRepairLineDto));
    }

    @Test
    void updateNonexistentRepairLineThrowsEntityNotFoundException() {
        final RepairEntity repairEntity = setupMockRepair();
        when(repairLineRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String repairLineId = UUID.randomUUID().toString();
        final UpdateRepairLineDto updateRepairLineDto = repairLineTestUtils.generateMockUpdateDto();

        assertThrows(EntityNotFoundException.class, () -> repairService.updateRepairLine(repairEntity.getId().toString(), repairLineId, updateRepairLineDto));
    }

    @Test
    void updateRepairLineThatDoesNotMatchRepairIdThrowsEntityNotFoundException() {
        final RepairEntity repairEntity1 = setupMockRepair();
        final RepairEntity repairEntity2 = repairTestUtils.generateMockEntity();
        final RepairLineEntity repairLineEntity = repairLineTestUtils.generateMockEntity(repairEntity2);
        when(repairLineRepository.findById(any(UUID.class))).thenReturn(Optional.of(repairLineEntity));
        final UpdateRepairLineDto updateRepairLineDto = repairLineTestUtils.generateMockUpdateDto();
        final String repairId = repairEntity1.getId().toString();
        final String repairLineId = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.updateRepairLine(repairId, repairLineId, updateRepairLineDto));
    }

    @Test
    void updateReturnLineReturnsUpdatedRepairLineEntity() {
        final RepairEntity repairEntity = setupMockRepair();
        final RepairLineEntity repairLineEntity = repairLineTestUtils.generateMockEntity(repairEntity);
        when(repairLineRepository.findById(any(UUID.class))).thenReturn(Optional.of(repairLineEntity));
        when(repairLineRepository.save(any(RepairLineEntity.class))).thenAnswer(i -> i.getArgument(0));
        final UpdateRepairLineDto updateRepairLineDto = repairLineTestUtils.generateMockUpdateDto();
        final String repairId = repairEntity.getId().toString();
        final String lineId = repairLineEntity.getId().toString();

        final RepairLineEntity updatedRepairLineEntity = repairService.updateRepairLine(repairId, lineId, updateRepairLineDto);

        assertThat(updatedRepairLineEntity).isNotNull();
        assertThat(updatedRepairLineEntity.getName()).isEqualTo(updateRepairLineDto.getName());
        assertThat(updatedRepairLineEntity.getAmount()).isEqualTo(updateRepairLineDto.getAmount());
        assertThat(updatedRepairLineEntity.getPrice()).isEqualTo(updateRepairLineDto.getPrice());
        assertThat(updatedRepairLineEntity.getType().getValue()).isEqualTo(updateRepairLineDto.getType());
    }

    @Test
    void deleteNonexistentRepairLineThrowsEntityNotFoundException() {
        final RepairEntity repairEntity = setupMockRepair();
        when(repairLineRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String repairId = repairEntity.getId().toString();
        final String lineId = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.deleteRepairLine(repairId, lineId));
    }

    @Test
    void deleteRepairLineThatDoesNotMatchRepairIdThrowsEntityNotFoundException() {
        final RepairEntity repairEntity1 = setupMockRepair();
        final RepairEntity repairEntity2 = repairTestUtils.generateMockEntity();
        final RepairLineEntity repairLineEntity = repairLineTestUtils.generateMockEntity(repairEntity2);
        when(repairLineRepository.findById(any(UUID.class))).thenReturn(Optional.of(repairLineEntity));
        final String repairId = repairEntity1.getId().toString();
        final String repairLineId = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> repairService.deleteRepairLine(repairId, repairLineId));
    }

}
