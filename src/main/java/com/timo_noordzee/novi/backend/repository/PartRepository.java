package com.timo_noordzee.novi.backend.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.timo_noordzee.novi.backend.data.PartEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.UUID;

public interface PartRepository extends EntityGraphJpaRepository<PartEntity, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE part p set p.stock = (p.stock - :amount) WHERE p.id = :id")
    void decrementStock(@Param("id") UUID id, @Param("amount") int amount);

}
