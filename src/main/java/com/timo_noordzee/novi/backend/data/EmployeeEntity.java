package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.timo_noordzee.novi.backend.domain.Role;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "employee")
@Table(indexes = {
        @Index(name = "idx_employee_email", columnList = "email")
})
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = EmployeeEntity.GRAPH_DEFAULT),
})
public class EmployeeEntity {

    public static final String GRAPH_DEFAULT = "Employee.default";

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
