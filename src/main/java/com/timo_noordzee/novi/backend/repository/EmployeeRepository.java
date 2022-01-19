package com.timo_noordzee.novi.backend.repository;

import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {
}
