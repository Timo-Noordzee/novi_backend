package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import com.timo_noordzee.novi.backend.mapper.ShortcomingMapper;
import com.timo_noordzee.novi.backend.repository.ShortcomingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ShortcomingService extends BaseRestService<ShortcomingEntity, UUID, CreateShortcomingDto, UpdateShortcomingDto, ShortcomingRepository, ShortcomingMapper> {

    private final VehicleService vehicleService;

    public ShortcomingService(final ShortcomingRepository repository, final ShortcomingMapper mapper, final VehicleService vehicleService) {
        super(repository, mapper);
        this.vehicleService = vehicleService;
    }

    @Override
    protected Optional<ShortcomingEntity> findById(final UUID uuid) {
        return repository.findById(uuid, EntityGraphs.named(ShortcomingEntity.GRAPH_WITH_VEHICLE));
    }

    @Override
    protected ShortcomingEntity fromCreateDto(final CreateShortcomingDto createDto) {
        final ShortcomingEntity shortcomingEntity = mapper.fromCreateDto(createDto);
        final VehicleEntity vehicleEntity = vehicleService.getById(createDto.getVehicleId());
        shortcomingEntity.setVehicle(vehicleEntity);
        return shortcomingEntity;
    }

    @Override
    UUID parseId(final String id) {
        return parseUUID(id);
    }

    @Override
    String entityType() {
        return ShortcomingEntity.class.getSimpleName();
    }
}
