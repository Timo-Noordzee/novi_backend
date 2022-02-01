package com.timo_noordzee.novi.backend.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.timo_noordzee.novi.backend.domain.InvoiceStatus;
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
    private UUID id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "paid_at")
    private Date paidAt;

    @Column(name = "status")
    private InvoiceStatus status;

    @Lob
    @Column(name = "data")
    @Type(type = "org.hibernate.type.BinaryType")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private byte[] data;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

}