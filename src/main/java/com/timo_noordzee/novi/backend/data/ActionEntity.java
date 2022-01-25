package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "action")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = ActionEntity.GRAPH_DEFAULT),
})
public class ActionEntity {

    public static final String GRAPH_DEFAULT = "Action.default";

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

}
