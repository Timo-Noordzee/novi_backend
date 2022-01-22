package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    private UUID id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<VehicleEntity> vehicles;

}
