package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.EmployeeEntity;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends EntityGraphJpaRepository<EmployeeEntity, UUID> {
    boolean existsByEmail(final String email);

    Optional<EmployeeEntity> findByEmail(final String email);
}
