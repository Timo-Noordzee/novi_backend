package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.timo_noordzee.novi.backend.domain.RepairStatus;
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
    @Schema(example = "0ec4abcc-f4df-4ef6-ae45-a71ecd05a316", format = "uuid")
    private UUID id;

    @Column(name = "remarks")
    @Schema(example = "Klant gaat niet akkoord met de reparatie")
    private String remarks;

    @Column(name = "status")
    @Schema(example = "1", allowableValues = {"0", "1", "2", "3'"})
    private RepairStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    @Schema(example = "2021-07-22 23:48:03.000000", format = "date-time")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(implementation = VehicleEntity.class)
    private VehicleEntity vehicle;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "repair", orphanRemoval = true)
    @ArraySchema(schema = @Schema(implementation = RepairLineEntity.class))
    private List<RepairLineEntity> lines;

}
