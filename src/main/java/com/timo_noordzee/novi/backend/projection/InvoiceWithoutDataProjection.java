package com.timo_noordzee.novi.backend.projection;

import com.timo_noordzee.novi.backend.domain.InvoiceStatus;

import java.util.Date;
import java.util.UUID;

public interface InvoiceWithoutDataProjection {
    UUID getId();

    Date getCreatedAt();

    Date getPaidAt();

    InvoiceStatus getStatus();
}
