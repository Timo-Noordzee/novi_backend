package com.timo_noordzee.novi.backend.projection;

import java.util.Date;
import java.util.UUID;

public interface VehiclePapersWithoutDataProjection {
    UUID getId();
    String getName();
    String getType();
    Date getUploadedAt();
}
