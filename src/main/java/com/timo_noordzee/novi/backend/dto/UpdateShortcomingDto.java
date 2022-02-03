package com.timo_noordzee.novi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateShortcomingDto {

    @Schema(example = "De lamp links achter werkt niet meer")
    private String description;

}
