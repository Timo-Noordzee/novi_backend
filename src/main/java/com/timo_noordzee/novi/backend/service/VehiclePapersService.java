package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.domain.VehiclePapersWithoutData;
import com.timo_noordzee.novi.backend.dto.CreateVehiclePapersDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehiclePapersDto;
import com.timo_noordzee.novi.backend.exception.FileUploadException;
import com.timo_noordzee.novi.backend.exception.ForbiddenFileTypeException;
import com.timo_noordzee.novi.backend.mapper.VehiclePapersMapper;
import com.timo_noordzee.novi.backend.repository.VehiclePapersRepository;
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
        try {
            final String originalFilename = multipartFile.getOriginalFilename();
            assert originalFilename != null;
            final int startIndex = originalFilename.replaceAll("\\\\", "/").lastIndexOf("/");
            final String fileName = originalFilename.substring(startIndex + 1);
            final CreateVehiclePapersDto createVehiclePapersDto = CreateVehiclePapersDto.builder()
                    .name(fileName)
                    .type(multipartFile.getContentType())
                    .data(multipartFile.getBytes())
                    .vehicleId(vehicleId)
                    .build();
            return add(createVehiclePapersDto);
        } catch (final IOException ioException) {
            throw new FileUploadException(ioException.getMessage());
        }
    }

    private List<VehiclePapersEntity> convertProjectionToEntity(final List<VehiclePapersWithoutData> papersWithoutData) {
        return papersWithoutData.stream()
                .map(vehiclePapersWithoutData -> VehiclePapersEntity.builder()
                        .id(vehiclePapersWithoutData.getId())
                        .name(vehiclePapersWithoutData.getName())
                        .type(vehiclePapersWithoutData.getType())
                        .uploadedAt(vehiclePapersWithoutData.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<VehiclePapersEntity> findAllForVehicle(final String vin) {
        final VehicleEntity vehicleEntity = vehicleService.getById(vin);
        return convertProjectionToEntity(repository.findAllByVehicle(vehicleEntity));
    }

    @Override
    protected void validateCreateConstrains(final CreateVehiclePapersDto createDto) {
        final String fileType = createDto.getType();
        if (!StringUtils.equals(fileType, MediaType.APPLICATION_PDF_VALUE)) {
            throw new ForbiddenFileTypeException(fileType);
        }
        super.validateCreateConstrains(createDto);
    }

    @Override
    protected List<VehiclePapersEntity> findAll() {
        return convertProjectionToEntity(repository.findAllProjectedBy());
    }

    @Override
    protected VehiclePapersEntity fromCreateDto(final CreateVehiclePapersDto createDto) {
        final VehicleEntity vehicleEntity = vehicleService.getById(createDto.getVehicleId());
        final VehiclePapersEntity vehiclePapersEntity = mapper.fromCreateDto(createDto);
        vehiclePapersEntity.setVehicle(vehicleEntity);
        return vehiclePapersEntity;
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
