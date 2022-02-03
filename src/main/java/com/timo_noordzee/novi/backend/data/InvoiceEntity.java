package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.timo_noordzee.novi.backend.domain.InvoiceStatus;
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
@Entity(name = "invoice")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = InvoiceEntity.GRAPH_DEFAULT),
})
public class InvoiceEntity {

    public static final String GRAPH_DEFAULT = "Invoice.default";

    @Id
    @Column(name = "id", unique = true)
    @Schema(example = "1706b5c5-da10-42d9-b450-c0c9810d18d4", format = "uuid")
    private UUID id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "paid_at")
    @Schema(example = "2022-01-28 10:49:08.000000", format = "date-time", nullable = true)
    private Date paidAt;

    @Column(name = "status")
    @Schema(example = "1", allowableValues = {"0", "1"})
    private InvoiceStatus status;

    @Lob
    @Column(name = "data")
    @Type(type = "org.hibernate.type.BinaryType")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(format = "byte", nullable = true)
    private byte[] data;

    @CreationTimestamp
    @Column(name = "created_at")
    @Schema(example = "2022-02-01 23:49:27.651000", format = "date-time")
    private Date createdAt;

}