package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "customer")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = CustomerEntity.GRAPH_DEFAULT),
        @NamedEntityGraph(name = CustomerEntity.GRAPH_WITH_VEHICLES, attributeNodes = {
                @NamedAttributeNode("vehicles")
        })
})
public class CustomerEntity {

    public static final String GRAPH_DEFAULT = "Customer.default";
    public static final String GRAPH_WITH_VEHICLES = "Customer.vehicles";

    @Id
    @Column(name = "id", unique = true)
    @Schema(example = "07860e82-a422-4d97-a7fb-270e3cf0cd2c", format = "uuid")
    private UUID id;

    @Column(name = "email", unique = true)
    @Schema(example = "luca.van.beek@klant.novi-garage.nl", format = "email")
    private String email;

    @Column(name = "phone")
    @Schema(example = "+31 6 23456789")
    private String phone;

    @Column(name = "name")
    @Schema(example = "Luca")
    private String name;

    @Column(name = "surname")
    @Schema(example = "van Beek")
    private String surname;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    @ArraySchema(schema = @Schema(implementation = VehicleEntity.class))
    private List<VehicleEntity> vehicles;

}
