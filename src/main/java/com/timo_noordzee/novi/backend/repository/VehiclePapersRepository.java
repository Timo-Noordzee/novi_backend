package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.domain.VehiclePapersWithoutData;

import java.util.List;
import java.util.UUID;

public interface VehiclePapersRepository extends EntityGraphJpaRepository<VehiclePapersEntity, UUID> {
    List<VehiclePapersWithoutData> findAllProjectedBy();

    List<VehiclePapersWithoutData> findAllByVehicle(final VehicleEntity vehicleEntity);
}
