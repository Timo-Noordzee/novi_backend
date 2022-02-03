package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.timo_noordzee.novi.backend.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "41842996-06ee-4c3c-990f-376a47c2342e", format = "uuid")
    private UUID id;

    @Column(name = "email", unique = true)
    @Schema(example = "arend.de.porter@novi-garage.nl", format = "email")
    private String email;

    @Column(name = "name")
    @Schema(example = "Arend")
    private String name;

    @Column(name = "surname")
    @Schema(example = "de Porter")
    private String surname;

    @JsonIgnore
    @Column(name = "password")
    @Schema(hidden = true)
    private String password;

    @Column(name = "role")
    @Schema(example = "backoffice", allowableValues = {"admin", "administrative", "backoffice", "cashier", "mechanic"})
    private Role role;

}
