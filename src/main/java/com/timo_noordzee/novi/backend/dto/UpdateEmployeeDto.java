package com.timo_noordzee.novi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEmployeeDto {

    @Schema(example = "Marko")
    private String name;

    @Schema(example = "Valder")
    private String surname;

    @Schema(example = "M5sj-qJE")
    private String password;

    @Schema(example = "admin", allowableValues = {"admin", "administrative", "backoffice", "cashier", "mechanic"})
    private String role;

}
