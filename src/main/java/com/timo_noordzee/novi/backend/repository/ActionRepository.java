package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.ActionEntity;

import java.util.UUID;

public interface ActionRepository extends EntityGraphJpaRepository<ActionEntity, UUID> {
}
