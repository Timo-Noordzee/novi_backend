package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.RepairEntity;

import java.util.UUID;

public interface RepairRepository extends EntityGraphJpaRepository<RepairEntity, UUID> {
}
