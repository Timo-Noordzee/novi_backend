package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateVehicleDto implements CreateDto {

    @Schema(example = "1FTPW14V87FA46384", format = "vin", externalDocs = @ExternalDocumentation(
            url = "https://www.iso.org/standard/52200.html",
            description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
    ))
    @NotBlank(message = "field is required")
    private String vin;

    @Schema(example = "G-373-LB", format = "license")
    @NotBlank(message = "field is required")
    private String license;

    @Schema(example = "BMW")
    @NotBlank(message = "field is required")
    private String brand;

    @Schema(example = "330I")
    @NotBlank(message = "field is required")
    private String make;

    @Schema(example = "2019")
    @NotNull(message = "field is required")
    @Min(message = "the minimum value is 1886", value = 1886)
    @Max(message = "the maximum value is 2032", value = 2032)
    private Integer year;

    @Schema(example = "8102cd51-1f2f-4051-974f-b20dda3ea5ec", format = "uuid")
    @NotBlank(message = "field is required")
    private String customerId;

    @Override
    @Schema(hidden = true)
    public String getId() {
        return vin;
    }
}
