package com.timo_noordzee.novi.backend.domain;

import java.util.Date;
import java.util.UUID;

public interface VehiclePapersWithoutData {
    UUID getId();
    String getName();
    String getType();
    Date getUploadedAt();
}
