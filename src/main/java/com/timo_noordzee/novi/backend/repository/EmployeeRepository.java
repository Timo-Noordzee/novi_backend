package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.EmployeeEntity;

import java.util.UUID;

public interface EmployeeRepository extends EntityGraphJpaRepository<EmployeeEntity, UUID> {
    boolean existsByEmail(final String email);
}
