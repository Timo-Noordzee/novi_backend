package com.timo_noordzee.novi.backend.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@Builder
public class UpdateVehicleDto {

    private String license;

    private String brand;

    private String make;

    @Min(message = "the minimum value is 1886", value = 1886)
    private Integer year;

    private String customerId;

}
