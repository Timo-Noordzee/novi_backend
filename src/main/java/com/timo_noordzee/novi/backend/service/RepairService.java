package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.timo_noordzee.novi.backend.data.*;
import com.timo_noordzee.novi.backend.domain.RepairLineType;
import com.timo_noordzee.novi.backend.dto.AddRepairLinesDto;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairLineDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.OutOfStockException;
import com.timo_noordzee.novi.backend.mapper.RepairLineMapper;
import com.timo_noordzee.novi.backend.mapper.RepairMapper;
import com.timo_noordzee.novi.backend.repository.RepairLineRepository;
import com.timo_noordzee.novi.backend.repository.RepairRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RepairService extends BaseRestService<RepairEntity, UUID, CreateRepairDto, UpdateRepairDto, RepairRepository, RepairMapper> {

    private final VehicleService vehicleService;
    private final PartService partService;
    private final ActionService actionService;
    private final RepairLineRepository repairLineRepository;
    private final RepairLineMapper repairLineMapper;

    public RepairService(
            final RepairRepository repository,
            final RepairMapper mapper,
            final VehicleService vehicleService,
            final PartService partService,
            final ActionService actionService,
            final RepairLineRepository repairLineRepository,
            final RepairLineMapper repairLineMapper
    ) {
        super(repository, mapper);
        this.vehicleService = vehicleService;
        this.partService = partService;
        this.actionService = actionService;
        this.repairLineRepository = repairLineRepository;
        this.repairLineMapper = repairLineMapper;
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
        final RepairEntity repairEntity = repository.findById(uuid, EntityGraphs.named(RepairEntity.GRAPH_DETAILED))
                .orElseThrow(() -> new EntityNotFoundException(uuid.toString(), entityType()));
        if (repairEntity.getLines().isEmpty()) {
            final List<RepairLineEntity> repairLineEntities = repairLineRepository.findAllByRepair(repairEntity);
            repairEntity.getLines().addAll(repairLineEntities);
        }
        return Optional.of(repairEntity);
    }

    protected RepairEntity findById(final String id) {
        final UUID uuid = parseUUID(id);
        return repository.findById(uuid, EntityGraphs.named(RepairEntity.GRAPH_DETAILED))
                .orElseThrow(() -> new EntityNotFoundException(entityType(), id));
    }

    protected RepairLineEntity findRepairLineById(final String id) {
        final UUID uuid = parseUUID(id);
        return repairLineRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException(RepairLineEntity.class.getSimpleName(), id));
    }

    @Override
    protected RepairEntity fromCreateDto(final CreateRepairDto createDto) {
        final RepairEntity repairEntity = mapper.fromCreateDto(createDto);
        final VehicleEntity vehicleEntity = vehicleService.getById(createDto.getVehicleId());
        repairEntity.setVehicle(vehicleEntity);
        return repairEntity;
    }

    @Transactional()
    public void addLinesToRepair(final String repairId, final AddRepairLinesDto addRepairLinesDto) {
        final RepairEntity repairEntity = findById(repairId);

        // Group Part lines by ID and sum the amount to aggregate duplicates
        final List<AddRepairLinesDto.Part> partList = addRepairLinesDto.getParts().stream()
                .collect(Collectors.groupingBy(AddRepairLinesDto.Part::getId))
                .entrySet().stream().map(entry -> {
                    final int total = entry.getValue().stream().map(AddRepairLinesDto.Part::getAmount).reduce(0, Integer::sum);
                    return AddRepairLinesDto.Part.builder().id(entry.getKey()).amount(total).build();
                }).collect(Collectors.toList());

        final List<RepairLineEntity> repairLineEntities = partList.stream().map(dto -> {
            final int amount = dto.getAmount();
            final PartEntity partEntity = partService.getById(dto.getId());
            if (partEntity.getStock() < amount) {
                throw new OutOfStockException(partEntity.getId(), partEntity.getStock(), amount);
            }
            partService.decrementStock(partEntity.getId(), amount);

            return repairLineMapper.fromPartEntity(partEntity, repairEntity, amount);
        }).collect(Collectors.toList());

        // Group Action lines by ID and sum the amount to aggregate duplicates
        final List<AddRepairLinesDto.Part> actionList = addRepairLinesDto.getActions().stream()
                .collect(Collectors.groupingBy(AddRepairLinesDto.Action::getId))
                .entrySet().stream().map(entry -> {
                    final int total = entry.getValue().stream().map(AddRepairLinesDto.Action::getAmount).reduce(0, Integer::sum);
                    return AddRepairLinesDto.Part.builder().id(entry.getKey()).amount(total).build();
                }).collect(Collectors.toList());

        repairLineEntities.addAll(actionList.stream().map(dto -> {
            final ActionEntity actionEntity = actionService.getById(dto.getId());
            return repairLineMapper.fromActionEntity(actionEntity, repairEntity, dto.getAmount());
        }).collect(Collectors.toList()));

        repairLineEntities.addAll(addRepairLinesDto.getCustom().stream()
                .map(dto -> repairLineMapper.fromCustomDto(dto, repairEntity))
                .collect(Collectors.toList()));

        repairLineRepository.saveAll(repairLineEntities);
    }

    public RepairLineEntity updateRepairLine(final String repairId, final String repairLineId, final UpdateRepairLineDto updateRepairLineDto) {
        final RepairEntity repairEntity = findById(repairId);
        final RepairLineEntity repairLineEntity = findRepairLineById(repairLineId);
        if (repairEntity.getId() != repairLineEntity.getRepair().getId()) {
            throw new EntityNotFoundException(repairLineId, RepairLineType.class.getSimpleName(), repairId, RepairEntity.class.getSimpleName());
        }
        repairLineMapper.updateWithDto(updateRepairLineDto, repairLineEntity);
        return repairLineRepository.save(repairLineEntity);
    }

    public RepairLineEntity deleteRepairLine(final String repairId, final String repairLineId) {
        final RepairEntity repairEntity = findById(repairId);
        final RepairLineEntity repairLineEntity = findRepairLineById(repairLineId);
        if (repairEntity.getId() != repairLineEntity.getRepair().getId()) {
            throw new EntityNotFoundException(repairLineId, RepairLineType.class.getSimpleName(), repairId, RepairEntity.class.getSimpleName());
        }
        repairLineRepository.delete(repairLineEntity);
        return repairLineEntity;
    }
}
