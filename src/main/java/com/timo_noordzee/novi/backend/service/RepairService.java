package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.mapper.RepairMapper;
import com.timo_noordzee.novi.backend.repository.RepairRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RepairService extends BaseRestService<RepairEntity, UUID, CreateRepairDto, UpdateRepairDto, RepairRepository, RepairMapper> {

    private final VehicleService vehicleService;

    public RepairService(final RepairRepository repository, final RepairMapper mapper, final VehicleService vehicleService) {
        super(repository, mapper);
        this.vehicleService = vehicleService;
    }

    @Override
    UUID parseId(final String id) {
        return parseUUID(id);
    }

    @Override
    String entityType() {
        return RepairEntity.class.getSimpleName();
    }

    @Override
    protected Optional<RepairEntity> findById(final UUID uuid) {
        return repository.findById(uuid, EntityGraphs.named(RepairEntity.GRAPH_DETAILED));
    }

    @Override
    protected RepairEntity fromCreateDto(final CreateRepairDto createDto) {
        final RepairEntity repairEntity = mapper.fromCreateDto(createDto);
        final VehicleEntity vehicleEntity = vehicleService.getById(createDto.getVehicleId());
        repairEntity.setVehicle(vehicleEntity);
        return repairEntity;
    }
}
