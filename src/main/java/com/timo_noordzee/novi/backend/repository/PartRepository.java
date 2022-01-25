package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.PartEntity;

import java.util.UUID;

public interface PartRepository extends EntityGraphJpaRepository<PartEntity, UUID> {
}
