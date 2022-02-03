package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.timo_noordzee.novi.backend.domain.RepairLineType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "repair_line")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = RepairLineEntity.GRAPH_DEFAULT),
})
public class RepairLineEntity {

    public static final String GRAPH_DEFAULT = "RepairLine.default";

    @Id
    @Column(name = "id", unique = true)
    @Schema(example = "ceb26362-598b-4d47-b70d-b8a4208c0b4d", format = "uuid")
    private UUID id;

    @Column(name = "name")
    @Schema(example = "Ruitenwisser")
    private String name;

    @Column(name = "amount")
    @Schema(example = "2")
    private int amount;

    @Column(name = "price")
    @Schema(example = "20.13", format = "double")
    private double price;

    @Column(name = "type")
    @Schema(example = "0", allowableValues = {"0", "1"})
    private RepairLineType type;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id")
    @Schema(implementation = RepairEntity.class)
    private RepairEntity repair;

    public double getTotalPrice() {
        return amount * price;
    }

}
