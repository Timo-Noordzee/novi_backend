package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "vehicle_papers")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = VehiclePapersEntity.GRAPH_DEFAULT),
})
public class VehiclePapersEntity {

    public static final String GRAPH_DEFAULT = "VehiclePapers.default";

    @Id
    @Column(name = "id", unique = true)
    @Schema(example = "972f07aa-75ba-4047-83f9-f881947560ff", format = "uuid")
    private UUID id;

    @Column(name = "name")
    @Schema(example = "KentekenCheck 73-DT-BP.pdf")
    private String name;

    @Column(name = "type")
    @Schema(example = "application/pdf")
    private String type;

    @Lob
    @Column(name = "data")
    @Type(type = "org.hibernate.type.BinaryType")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(format = "byte")
    private byte[] data;

    @CreationTimestamp
    @Column(name = "uploaded_at")
    @Schema(example = "2022-02-01 23:06:14.953000", format = "date-time")
    private Date uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(implementation = VehicleEntity.class)
    private VehicleEntity vehicle;

}
