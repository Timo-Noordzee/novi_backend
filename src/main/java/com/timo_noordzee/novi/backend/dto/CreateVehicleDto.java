package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateVehicleDto implements CreateDto {

    @NotBlank(message = "field is required")
    private String vin;

    @NotBlank(message = "field is required")
    private String license;

    @NotBlank(message = "field is required")
    private String brand;

    @NotBlank(message = "field is required")
    private String make;

    @NotNull(message = "field is required")
    @Min(message = "the minimum value is 1886", value = 1886)
    @Max(message = "the maximum value is 2032", value = 2032)
    private Integer year;

    @NotBlank(message = "field is required")
    private String customerId;

    @Override
    public String getId() {
        return vin;
    }
}
