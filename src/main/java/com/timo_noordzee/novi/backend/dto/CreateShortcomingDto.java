package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CreateShortcomingDto implements CreateDto {

    private String id;

    @NotBlank(message = "field is required")
    private String description;

    @NotBlank(message = "field is required")
    private String vehicleId;

}
