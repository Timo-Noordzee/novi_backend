package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.InvoiceEntity;
import com.timo_noordzee.novi.backend.projection.InvoiceWithoutDataProjection;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends EntityGraphJpaRepository<InvoiceEntity, UUID> {
    List<InvoiceWithoutDataProjection> findAllProjectedBy();
}
