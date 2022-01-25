package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateActionDto implements CreateDto {

    private String id;

    @NotBlank(message = "field is required")
    private String name;

    @NotNull(message = "field is required")
    @Min(value = 0, message = "the minimum value is 0")
    private Double price;

}
