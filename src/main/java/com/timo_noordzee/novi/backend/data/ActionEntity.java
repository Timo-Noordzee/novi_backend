package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "1f48d182-35e2-497e-8725-a9c7f7e9f7c9", format = "uuid")
    private UUID id;

    @Column(name = "name")
    @Schema(example = "Koppeling vervangen")
    private String name;

    @Column(name = "price")
    @Schema(example = "199.95", format = "double")
    private double price;

}
