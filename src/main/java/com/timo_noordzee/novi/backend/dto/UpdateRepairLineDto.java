package com.timo_noordzee.novi.backend.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@Builder
public class UpdateRepairLineDto {

    private String name;

    @Min(value = 0, message = "the minimum value is 0")
    private Integer price;

    @Min(value = 1, message = "the minimum value is 1")
    private Integer amount;

    private Integer type;

}
