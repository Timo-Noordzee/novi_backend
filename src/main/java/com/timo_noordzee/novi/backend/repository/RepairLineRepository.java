package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.RepairLineEntity;

import java.util.List;
import java.util.UUID;

public interface RepairLineRepository extends EntityGraphJpaRepository<RepairLineEntity, UUID> {
    List<RepairLineEntity> findAllByRepair(final RepairEntity repairEntity);
}
