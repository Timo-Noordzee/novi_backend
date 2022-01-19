package com.timo_noordzee.novi.backend.repository;

import com.timo_noordzee.novi.backend.data.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    boolean existsByEmail(final String email);
}
