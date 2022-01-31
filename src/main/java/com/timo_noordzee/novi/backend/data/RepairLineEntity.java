package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.timo_noordzee.novi.backend.domain.RepairLineType;
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
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private int amount;

    @Column(name = "price")
    private double price;

    @Column(name = "type")
    private RepairLineType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id")
    @JsonIgnore
    private RepairEntity repair;

    public double getTotalPrice() {
        return amount * price;
    }

}
