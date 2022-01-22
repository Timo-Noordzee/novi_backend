package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "vehicle")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "vin")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = VehicleEntity.GRAPH_DEFAULT),
        @NamedEntityGraph(name = VehicleEntity.GRAPH_WITH_OWNER, attributeNodes = {
                @NamedAttributeNode("owner")
        })
})
public class VehicleEntity {

    public static final String GRAPH_DEFAULT = "Vehicle.default";
    public static final String GRAPH_WITH_OWNER = "Vehicle.owner";

    @Id
    @Column(name = "vin", unique = true)
    private String vin;

    @Column(name = "license", unique = true)
    private String license;

    @Column(name = "brand")
    private String brand;

    @Column(name = "make")
    private String make;

    @Column(name = "year")
    private int year;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private CustomerEntity owner;

}
