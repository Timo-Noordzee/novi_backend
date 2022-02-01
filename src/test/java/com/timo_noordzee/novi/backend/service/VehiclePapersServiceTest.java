package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.dto.UpdateVehiclePapersDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.FileUploadException;
import com.timo_noordzee.novi.backend.exception.ForbiddenFileTypeException;
import com.timo_noordzee.novi.backend.mapper.CustomerMapper;
import com.timo_noordzee.novi.backend.mapper.VehicleMapper;
import com.timo_noordzee.novi.backend.mapper.VehiclePapersMapper;
import com.timo_noordzee.novi.backend.projection.VehiclePapersWithoutDataProjection;
import com.timo_noordzee.novi.backend.repository.CustomerRepository;
import com.timo_noordzee.novi.backend.repository.VehiclePapersRepository;
import com.timo_noordzee.novi.backend.repository.VehicleRepository;
import com.timo_noordzee.novi.backend.util.VehiclePapersTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VehiclePapersServiceTest {

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final VehiclePapersTestUtils vehiclePapersTestUtils = new VehiclePapersTestUtils();

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private MultipartFile mockMultiPartFile;

    @Mock
    private VehiclePapersRepository vehiclePapersRepository;

    private VehiclePapersService vehiclePapersService;

    @BeforeEach
    void setup() {
        final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
        final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);
        final VehiclePapersMapper vehiclePapersMapper = Mappers.getMapper(VehiclePapersMapper.class);
        final CustomerService customerService = new CustomerService(customerRepository, customerMapper);
        final VehicleService vehicleService = new VehicleService(vehicleRepository, vehicleMapper, customerService);
        vehiclePapersService = new VehiclePapersService(vehiclePapersRepository, vehiclePapersMapper, vehicleService);
    }

    @Test
    void contentTypeIsNotApplicationPdfThrowsForbiddenFileException() {
        final MultipartFile multipartFile = vehiclePapersTestUtils.generateMockMultipartFile(MediaType.IMAGE_PNG);
        final String vehicleId = vehicleTestUtils.randomVin();

        assertThrows(ForbiddenFileTypeException.class, () -> vehiclePapersService.add(vehicleId, multipartFile));
    }

    @Test
    void addingForNonexistentVehicleThrowsEntityNotFoundException() {
        final MultipartFile multipartFile = vehiclePapersTestUtils.generateMockMultipartFile(MediaType.APPLICATION_PDF);
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String vehicleId = vehicleTestUtils.randomVin();

        assertThrows(EntityNotFoundException.class, () -> vehiclePapersService.add(vehicleId, multipartFile));
    }

    @Test
    void addValidFileForExistingVehicleReturnsVehiclePapersEntity() throws IOException {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        final MultipartFile multipartFile = vehiclePapersTestUtils.generateMockMultipartFile(MediaType.APPLICATION_PDF);
        when(vehiclePapersRepository.save(any(VehiclePapersEntity.class))).thenAnswer(i -> {
            final VehiclePapersEntity vehiclePapersEntity = i.getArgument(0);
            vehiclePapersEntity.setUploadedAt(new Date());
            return vehiclePapersEntity;
        });

        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersService.add(vehicleEntity.getVin(), multipartFile);

        assertThat(vehiclePapersEntity).isNotNull();
        assertThat(vehiclePapersEntity.getType()).isEqualTo(multipartFile.getContentType());
        assertThat(vehiclePapersEntity.getData()).isEqualTo(multipartFile.getBytes());
        assertThat(vehiclePapersEntity.getUploadedAt()).isBeforeOrEqualTo(new Date());
    }

    @Test
    void updateVehiclePapersThrowsNotImplementedException() {
        final String id = UUID.randomUUID().toString();
        final UpdateVehiclePapersDto updateVehiclePapersDto = new UpdateVehiclePapersDto();

        assertThrows(NotImplementedException.class, () -> vehiclePapersService.update(id, updateVehiclePapersDto));
    }

    @Test
    void convertingVehiclePapersWithoutDataProjectionToVehiclePapersEntityWorks() {
        final List<VehiclePapersWithoutDataProjection> vehiclePapersWithoutDataProjections = new ArrayList<>();
        final VehiclePapersWithoutDataProjection projection = vehiclePapersTestUtils.generateMockVehiclePapersWithoutDataProjection();
        vehiclePapersWithoutDataProjections.add(projection);
        when(vehiclePapersRepository.findAllProjectedBy()).thenReturn(vehiclePapersWithoutDataProjections);

        final List<VehiclePapersEntity> vehiclePapersEntityList = vehiclePapersService.findAll();

        assertThat(vehiclePapersEntityList).hasSize(1);
        assertThat(vehiclePapersEntityList.get(0).getId()).isEqualTo(projection.getId());
        assertThat(vehiclePapersEntityList.get(0).getName()).isEqualTo(projection.getName());
        assertThat(vehiclePapersEntityList.get(0).getType()).isEqualTo(projection.getType());
        assertThat(vehiclePapersEntityList.get(0).getData()).isNull();
        assertThat(vehiclePapersEntityList.get(0).getUploadedAt()).isEqualTo(projection.getUploadedAt());
    }

    @Test
    void getByIdForNonexistentVehiclePapersThrowsEntityNotFoundException() {
        when(vehiclePapersRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> vehiclePapersService.getById(id));
    }

    @Test
    void addWithInvalidFileThrowsFileUploadException() throws IOException {
        final MultipartFile multipartFile = vehiclePapersTestUtils.generateMockMultipartFile(MediaType.APPLICATION_PDF);
        when(mockMultiPartFile.getOriginalFilename()).thenReturn(multipartFile.getOriginalFilename());
        when(mockMultiPartFile.getContentType()).thenReturn(multipartFile.getContentType());
        when(mockMultiPartFile.getBytes()).thenThrow(new IOException());
        final String id = UUID.randomUUID().toString();

        assertThrows(FileUploadException.class, () -> vehiclePapersService.add(id, mockMultiPartFile));
    }

    @Test
    void findAllForVehicleWithNonexistentVehicleThrowsEntityNotFoundException() {
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.empty());
        final String vehicleId = vehicleTestUtils.randomVin();

        assertThrows(EntityNotFoundException.class, () -> vehiclePapersService.findAllForVehicle(vehicleId));
    }

    @Test
    void findAllForVehicleConvertsProjectionToVehiclePapersEntityList() {
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity();
        when(vehicleRepository.findById(any(String.class), any(EntityGraph.class))).thenReturn(Optional.of(vehicleEntity));
        final VehiclePapersWithoutDataProjection projection = vehiclePapersTestUtils.generateMockVehiclePapersWithoutDataProjection();
        final List<VehiclePapersWithoutDataProjection> vehiclePapersWithoutDataProjections = new ArrayList<>();
        vehiclePapersWithoutDataProjections.add(projection);
        when(vehiclePapersRepository.findAllByVehicle(any(VehicleEntity.class))).thenReturn(vehiclePapersWithoutDataProjections);

        final List<VehiclePapersEntity> vehiclePapersEntityList = vehiclePapersService.findAllForVehicle(vehicleEntity.getVin());

        assertThat(vehiclePapersEntityList).hasSize(1);
        assertThat(vehiclePapersEntityList.get(0).getId()).isEqualTo(projection.getId());
        assertThat(vehiclePapersEntityList.get(0).getName()).isEqualTo(projection.getName());
        assertThat(vehiclePapersEntityList.get(0).getType()).isEqualTo(projection.getType());
        assertThat(vehiclePapersEntityList.get(0).getData()).isNull();
        assertThat(vehiclePapersEntityList.get(0).getUploadedAt()).isEqualTo(projection.getUploadedAt());
    }

}
