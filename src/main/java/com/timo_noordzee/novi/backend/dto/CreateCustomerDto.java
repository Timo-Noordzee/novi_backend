package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CreateCustomerDto implements CreateDto {

    @Schema(example = "8102cd51-1f2f-4051-974f-b20dda3ea5ec", format = "uuid")
    private String id;

    @Schema(example = "Gert")
    @NotBlank(message = "field is required")
    private String name;

    @Schema(example = "Veldman")
    @NotBlank(message = "field is required")
    private String surname;

    @Schema(example = "gert.veldman@klant.novi-garage.nl", format = "email")
    @Email(message = "isn't a valid email address")
    @NotBlank(message = "field is required")
    private String email;

    @Schema(example = "+31 6 12345678")
    @NotBlank(message = "field is required")
    private String phone;

}
