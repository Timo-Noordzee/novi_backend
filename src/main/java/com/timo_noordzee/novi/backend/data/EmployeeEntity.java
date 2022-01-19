package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.timo_noordzee.novi.backend.domain.Role;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity(name = "employee")
public class EmployeeEntity {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private Role role;
    
}
