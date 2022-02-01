package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.timo_noordzee.novi.backend.data.*;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import com.timo_noordzee.novi.backend.exception.*;
import com.timo_noordzee.novi.backend.mapper.*;
import com.timo_noordzee.novi.backend.projection.InvoiceWithoutDataProjection;
import com.timo_noordzee.novi.backend.repository.*;
import com.timo_noordzee.novi.backend.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext
public class InvoiceServiceTest {

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

    @Mock
    private InvoiceRepository invoiceRepository;

    private InvoiceService invoiceService;

    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final RepairTestUtils repairTestUtils = new RepairTestUtils();
    private final RepairLineTestUtils repairLineTestUtils = new RepairLineTestUtils();
    private final InvoiceTestUtils invoiceTestUtils = new InvoiceTestUtils();

    @BeforeEach
    void setUp() {
        final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
        final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);
        final PartMapper partMapper = Mappers.getMapper(PartMapper.class);
        final ActionMapper actionMapper = Mappers.getMapper(ActionMapper.class);
        final RepairMapper repairMapper = Mappers.getMapper(RepairMapper.class);
        final RepairLineMapper repairLineMapper = Mappers.getMapper(RepairLineMapper.class);
        final InvoiceMapper invoiceMapper = Mappers.getMapper(InvoiceMapper.class);
        final CustomerService customerService = new CustomerService(customerRepository, customerMapper);
        final VehicleService vehicleService = new VehicleService(vehicleRepository, vehicleMapper, customerService);
        final PartService partService = new PartService(partRepository, partMapper);
        final ActionService actionService = new ActionService(actionRepository, actionMapper);
        final RepairService repairService = new RepairService(repairRepository, repairMapper, vehicleService, partService, actionService, repairLineRepository, repairLineMapper);
        invoiceService = new InvoiceService(invoiceRepository, invoiceMapper, repairService);
    }

    @Test
    void parsingInvalidIdThrowsInvalidUUIDException() {
        final String invalidUUID = "invalid-id";

        assertThrows(InvalidUUIDException.class, () -> invoiceService.parseId(invalidUUID));
    }

    @Test
    void getByIdForNonexistentInvoiceThrowsEntityNotFoundException() {
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> invoiceService.getById(id));
    }

    @Test
    void findAllReturnsInvoiceEntityListWithoutData() {
        final List<InvoiceWithoutDataProjection> invoiceWithoutDataProjectionList = new ArrayList<>();
        final InvoiceWithoutDataProjection projection = invoiceTestUtils.generateMockProjection();
        invoiceWithoutDataProjectionList.add(projection);
        when(invoiceRepository.findAllProjectedBy()).thenReturn(invoiceWithoutDataProjectionList);

        final List<InvoiceEntity> invoiceEntityList = invoiceService.findAll();

        assertThat(invoiceEntityList).hasSize(1);
        assertThat(invoiceEntityList.get(0).getId()).isEqualTo(projection.getId());
        assertThat(invoiceEntityList.get(0).getStatus()).isEqualTo(projection.getStatus());
        assertThat(invoiceEntityList.get(0).getCreatedAt()).isEqualTo(projection.getCreatedAt());
        assertThat(invoiceEntityList.get(0).getPaidAt()).isEqualTo(projection.getPaidAt());
        assertThat(invoiceEntityList.get(0).getData()).isNull();
    }

    @Test
    void addInvoiceWithAlreadyExistingIdThrowsEntityAlreadyExistsExceeption() {
        when(invoiceRepository.existsById(any(UUID.class))).thenReturn(true);
        final String repairId = UUID.randomUUID().toString();
        final CreateInvoiceDto createInvoiceDto = invoiceTestUtils.generateMockCreateDto(repairId);
        createInvoiceDto.setId(UUID.randomUUID().toString());

        assertThrows(EntityAlreadyExistsException.class, () -> invoiceService.add(createInvoiceDto));
    }

    @Test
    void addInvoiceForNonexistentRepairThrowsEntityNotFoundException() {
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String repairId = UUID.randomUUID().toString();
        final CreateInvoiceDto createInvoiceDto = invoiceTestUtils.generateMockCreateDto(repairId);

        assertThrows(EntityNotFoundException.class, () -> invoiceService.add(createInvoiceDto));
    }

    @Test
    void addInvoiceWithMissingDataThrowsGenerateInvoiceException() {
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity();
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.of(repairEntity));
        final CreateInvoiceDto createInvoiceDto = invoiceTestUtils.generateMockCreateDto(repairEntity.getId().toString());

        // Exception is thrown because vehicle and vehicle.owner are null but required in template
        assertThrows(GenerateInvoiceException.class, () -> invoiceService.add(createInvoiceDto));
    }

    @Test
    void addWithInvalidStatusThrowsUnknownStatusException() {
        final String repairId = UUID.randomUUID().toString();
        final CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder().status(-1).repairId(repairId).build();

        assertThrows(UnknownStatusException.class, () -> invoiceService.add(createInvoiceDto));
    }

    @Test
    void addInvoiceWithValidPayloadReturnsInvoiceEntity() {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        for (int i = 0; i < 6; i++) {
            final RepairLineEntity repairLineEntity = repairLineTestUtils.generateMockEntity(repairEntity);
            repairEntity.getLines().add(repairLineEntity);
        }
        when(repairRepository.findById(any(UUID.class), any(EntityGraph.class))).thenReturn(Optional.of(repairEntity));
        final CreateInvoiceDto createInvoiceDto = invoiceTestUtils.generateMockCreateDto(repairEntity.getId().toString());
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenAnswer(i -> {
            final InvoiceEntity invoiceEntity = i.getArgument(0);
            invoiceEntity.setCreatedAt(new Date());
            return invoiceEntity;
        });

        final InvoiceEntity invoiceEntity = invoiceService.add(createInvoiceDto);

        assertThat(invoiceEntity).isNotNull();
        assertThat(invoiceEntity.getStatus().getValue()).isEqualTo(createInvoiceDto.getStatus());
    }

    @Test
    void updateNonexistentInvoiceThrowsEntityNotFoundException() {
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final UpdateInvoiceDto updateInvoiceDto = invoiceTestUtils.generateMockUpdateDto();
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> invoiceService.update(id, updateInvoiceDto));
    }

    @Test
    void updateExistingInvoiceReturnsUpdatedInvoice() {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(Optional.of(invoiceEntity));
        final UpdateInvoiceDto updateInvoiceDto = invoiceTestUtils.generateMockUpdateDto();
        final String id = invoiceEntity.getId().toString();
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenAnswer(i -> i.getArgument(0));

        final InvoiceEntity updatedInvoiceEntity = invoiceService.update(id, updateInvoiceDto);

        assertThat(updatedInvoiceEntity).isNotNull();
        assertThat(updatedInvoiceEntity.getId()).isEqualTo(invoiceEntity.getId());
        assertThat(updatedInvoiceEntity.getData()).isEqualTo(invoiceEntity.getData());
        assertThat(updatedInvoiceEntity.getStatus().getValue()).isEqualTo(updateInvoiceDto.getStatus());
        assertThat(updatedInvoiceEntity.getPaidAt()).isEqualTo(updatedInvoiceEntity.getPaidAt());
        assertThat(updatedInvoiceEntity.getCreatedAt()).isEqualTo(updatedInvoiceEntity.getCreatedAt());
    }

    @Test
    void deleteNonexistentInvoiceThrowsEntityNotFoundException() {
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> invoiceService.deleteById(id));
    }

    @Test
    void deleteExistingInvoiceReturnsDeletedInvoice() {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(Optional.of(invoiceEntity));
        final String id = invoiceEntity.getId().toString();

        final InvoiceEntity deletedInvoiceEntity = invoiceService.deleteById(id);

        assertThat(deletedInvoiceEntity).isNotNull();
        assertThat(deletedInvoiceEntity.getId()).isEqualTo(invoiceEntity.getId());
        assertThat(deletedInvoiceEntity.getData()).isEqualTo(invoiceEntity.getData());
        assertThat(deletedInvoiceEntity.getStatus()).isEqualTo(invoiceEntity.getStatus());
        assertThat(deletedInvoiceEntity.getPaidAt()).isEqualTo(invoiceEntity.getPaidAt());
        assertThat(deletedInvoiceEntity.getCreatedAt()).isEqualTo(invoiceEntity.getCreatedAt());
    }

}
