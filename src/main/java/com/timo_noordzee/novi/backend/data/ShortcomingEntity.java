package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Entity(name = "shortcoming")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = ShortcomingEntity.GRAPH_DEFAULT),
        @NamedEntityGraph(name = ShortcomingEntity.GRAPH_WITH_VEHICLE, attributeNodes = {
                @NamedAttributeNode("vehicle")
        })
})
public class ShortcomingEntity {

    public static final String GRAPH_DEFAULT = "Shortcoming.default";
    public static final String GRAPH_WITH_VEHICLE = "Shortcoming.vehicle";

    @Id
    @Column(name = "id", unique = true)
    @Schema(example = "f12163d8-d3dc-4d58-8960-2c3100545f6a", format = "uuid")
    private UUID id;

    @Column(name = "description")
    @Schema(example = "Remschijven kapot")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    @Schema(example = "2021-07-11 15:24:32.000000", format = "date-time")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(implementation = VehicleEntity.class)
    private VehicleEntity vehicle;

}
