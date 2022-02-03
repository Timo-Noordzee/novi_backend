package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateVehiclePapersDto implements CreateDto {

    private String id;

    private String name;

    private String type;

    private String vehicleId;

    private byte[] data;

}
