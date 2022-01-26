package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class CreateRepairDto implements CreateDto {

    private String id;

    private String remarks;

    private int status;

    @NotEmpty(message = "field is required")
    private String vehicleId;

}
