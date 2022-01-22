package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.CustomerEntity;

import java.util.UUID;

public interface CustomerRepository extends EntityGraphJpaRepository<CustomerEntity, UUID> {
    boolean existsByEmail(final String email);
}
