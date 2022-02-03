package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreatePartDto implements CreateDto {

    @Schema(example = "1bde1162-7da8-4a5b-b76c-becd8b7f9173", format = "uuid")
    private String id;

    @Schema(example = "Motorblok")
    @NotBlank(message = "field is required")
    private String name;

    @Schema(example = "59.99", format = "double")
    @NotNull(message = "field is required")
    @Min(value = 0, message = "the minimum value is 0")
    private Double price;

    @Schema(example = "5")
    @Min(value = 0, message = "the minimum value is 0")
    private int stock;

}
