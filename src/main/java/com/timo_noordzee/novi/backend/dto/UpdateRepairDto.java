package com.timo_noordzee.novi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRepairDto {

    @Schema(example = "Reparatie voltooid, alle tekortkomingen zijn opgelost")
    private String remarks;

    @Schema(example = "3", allowableValues = {"0", "1", "2", "3"})
    private Integer status;

}
