package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "vehicle")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "vin")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = VehicleEntity.GRAPH_DEFAULT),
        @NamedEntityGraph(name = VehicleEntity.GRAPH_FULL_DETAILS, attributeNodes = {
                @NamedAttributeNode("owner"),
                @NamedAttributeNode("shortcomings"),
        })
})
public class VehicleEntity {

    public static final String GRAPH_DEFAULT = "Vehicle.default";
    public static final String GRAPH_FULL_DETAILS = "Vehicle.detailed";

    @Id
    @Column(name = "vin", unique = true)
    @Schema(example = "5J6RE4H42BL073812", format = "vin", externalDocs = @ExternalDocumentation(
            url = "https://www.iso.org/standard/52200.html",
            description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
    ))
    private String vin;

    @Column(name = "license", unique = true)
    @Schema(example = "73-DT-BP", format = "license")
    private String license;

    @Column(name = "brand")
    @Schema(example = "Opel")
    private String brand;

    @Column(name = "make")
    @Schema(example = "Corsa B")
    private String make;

    @Column(name = "year")
    @Schema(example = "1999")
    private int year;

    @CreationTimestamp
    @Column(name = "created_at")
    @Schema(example = "2022-02-01 22:36:54.000000", format = "date-time")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(implementation = CustomerEntity.class)
    private CustomerEntity owner;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "vehicle", orphanRemoval = true)
    @ArraySchema(schema = @Schema(implementation = ShortcomingEntity.class))
    private List<ShortcomingEntity> shortcomings;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "vehicle", orphanRemoval = true)
    @ArraySchema(schema = @Schema(implementation = VehiclePapersEntity.class))
    private List<VehiclePapersEntity> papers;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "vehicle", orphanRemoval = true)
    @ArraySchema(schema = @Schema(implementation = RepairEntity.class))
    private List<RepairEntity> vehicles;

}
