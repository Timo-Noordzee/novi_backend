package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.projection.VehiclePapersWithoutDataProjection;
import com.timo_noordzee.novi.backend.dto.CreateVehiclePapersDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehiclePapersDto;
import com.timo_noordzee.novi.backend.exception.FileUploadException;
import com.timo_noordzee.novi.backend.exception.ForbiddenFileTypeException;
import com.timo_noordzee.novi.backend.mapper.VehiclePapersMapper;
import com.timo_noordzee.novi.backend.repository.VehiclePapersRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehiclePapersService extends BaseRestService<VehiclePapersEntity, UUID, CreateVehiclePapersDto, UpdateVehiclePapersDto, VehiclePapersRepository, VehiclePapersMapper> {

    private final VehicleService vehicleService;

    public VehiclePapersService(final VehiclePapersRepository repository, final VehiclePapersMapper mapper, final VehicleService vehicleService) {
        super(repository, mapper);
        this.vehicleService = vehicleService;
    }

    public VehiclePapersEntity add(final String vehicleId, final MultipartFile multipartFile) {
        final String fileType = multipartFile.getContentType();
        if (!StringUtils.equals(fileType, MediaType.APPLICATION_PDF_VALUE)) {
            throw new ForbiddenFileTypeException(fileType);
        }

        try {
            final String originalFilename = multipartFile.getOriginalFilename();
            assert originalFilename != null;
            final int startIndex = originalFilename.replaceAll("\\\\", "/").lastIndexOf("/");
            final String fileName = originalFilename.substring(startIndex + 1);
            final CreateVehiclePapersDto createVehiclePapersDto = CreateVehiclePapersDto.builder()
                    .name(fileName)
                    .type(fileType)
                    .data(multipartFile.getBytes())
                    .vehicleId(vehicleId)
                    .build();
            return add(createVehiclePapersDto);
        } catch (final IOException ioException) {
            throw new FileUploadException(ioException.getMessage());
        }
    }

    private VehiclePapersEntity convertProjectionToEntity(final VehiclePapersWithoutDataProjection projection) {
        return VehiclePapersEntity.builder()
                .id(projection.getId())
                .name(projection.getName())
                .type(projection.getType())
                .uploadedAt(projection.getUploadedAt())
                .build();
    }

    public List<VehiclePapersEntity> findAllForVehicle(final String vin) {
        final VehicleEntity vehicleEntity = vehicleService.getById(vin);
        return repository.findAllByVehicle(vehicleEntity).stream()
                .map(this::convertProjectionToEntity)
                .collect(Collectors.toList());
    }

    @Override
    protected List<VehiclePapersEntity> findAll() {
        return repository.findAllProjectedBy().stream().map(this::convertProjectionToEntity).collect(Collectors.toList());
    }

    @Override
    protected VehiclePapersEntity fromCreateDto(final CreateVehiclePapersDto createDto) {
        final VehicleEntity vehicleEntity = vehicleService.getById(createDto.getVehicleId());
        final VehiclePapersEntity vehiclePapersEntity = mapper.fromCreateDto(createDto);
        vehiclePapersEntity.setVehicle(vehicleEntity);
        return vehiclePapersEntity;
    }

    @Override
    public VehiclePapersEntity update(final String id, final UpdateVehiclePapersDto updateDto) {
        throw new NotImplementedException();
    }

    @Override
    UUID parseId(final String id) {
        return parseUUID(id);
    }

    @Override
    String entityType() {
        return VehiclePapersEntity.class.getSimpleName();
    }
}
