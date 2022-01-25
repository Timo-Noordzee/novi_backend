package com.timo_noordzee.novi.backend.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@Builder
public class UpdatePartDto {

    private String name;

    @Min(value = 0, message = "the minimum value is 0")
    public Double price;

    @Min(value = 0, message = "the minimum value is 0")
    public Integer stock;

}
