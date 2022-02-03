package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CreateEmployeeDto implements CreateDto {

    @Schema(example = "74f61000-8193-4b75-9a50-3ced99961988", format = "uuid")
    private String id;

    @Schema(example = "Paulus")
    @NotBlank(message = "field is required")
    private String name;

    @Schema(example = "Broeren")
    @NotBlank(message = "field is required")
    private String surname;

    @Schema(example = "paulus-broeren@novi-garage.nl", format = "email")
    @Email(message = "isn't a valid email address")
    @NotBlank(message = "field is required")
    private String email;

    @Schema(example = "QZ-r6XA$")
    @NotBlank(message = "field is required")
    private String password;

    @Schema(example = "mechanic", allowableValues = {"admin", "administrative", "backoffice", "cashier", "mechanic"})
    @NotBlank(message = "field is required")
    private String role;

}
