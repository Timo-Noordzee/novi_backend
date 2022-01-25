package com.timo_noordzee.novi.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRepairDto {

    private String remarks;

    private Integer status;

}
