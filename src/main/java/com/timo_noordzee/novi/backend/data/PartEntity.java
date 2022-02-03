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
@Entity(name = "part")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = PartEntity.GRAPH_DEFAULT),
})
public class PartEntity {

    public static final String GRAPH_DEFAULT = "Part.default";

    @Id
    @Column(name = "id", unique = true)
    @Schema(example = "f71f2846-4e53-45ac-b8c0-6edb93728698", format = "uuid")
    private UUID id;

    @Column(name = "name")
    @Schema(example = "Remblokken")
    private String name;

    @Column(name = "price")
    @Schema(example = "34.04", format = "double")
    private double price;

    @Column(name = "stock")
    @Schema(example = "10")
    private int stock;

}
