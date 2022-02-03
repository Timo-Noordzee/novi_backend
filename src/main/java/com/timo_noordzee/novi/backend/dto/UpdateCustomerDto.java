package com.timo_noordzee.novi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UpdateCustomerDto {

    @Schema(example = "Ingrid")
    private String name;

    @Schema(example = "de Vries")
    private String surname;

    @Schema(example = "ingrid.de.vries@klant.novi-garage.nl", format = "email")
    @Email(message = "isn't a valid email address")
    private String email;

    @Schema(example = "+31 6 87654321")
    private String phone;

}
