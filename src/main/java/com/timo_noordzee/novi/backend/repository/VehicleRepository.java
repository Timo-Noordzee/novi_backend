package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.VehicleEntity;

public interface VehicleRepository extends EntityGraphJpaRepository<VehicleEntity, String> {
    boolean existsByLicense(final String license);
}
