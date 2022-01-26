package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.timo_noordzee.novi.backend.domain.RepairStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "repair")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = RepairEntity.GRAPH_DEFAULT),
        @NamedEntityGraph(name = RepairEntity.GRAPH_DETAILED, attributeNodes = {
                @NamedAttributeNode("vehicle")
        })
})
public class RepairEntity {

    public static final String GRAPH_DEFAULT = "Repair.default";
    public static final String GRAPH_DETAILED = "Repair.detailed";

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "status")
    private RepairStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private VehicleEntity vehicle;

}
