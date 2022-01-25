package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateRepairDto implements CreateDto {

    private String id;

    private String remarks;

    @NotNull(message = "field is required")
    private Integer status;

    @NotEmpty(message = "field is required")
    private String vehicleId;

}
