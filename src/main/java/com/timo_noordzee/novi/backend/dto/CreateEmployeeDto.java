package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CreateEmployeeDto implements CreateDto {

    private String id;

    @NotBlank(message = "field is required")
    private String name;

    @NotBlank(message = "field is required")
    private String surname;

    @Email(message = "isn't a valid email address")
    @NotBlank(message = "field is required")
    private String email;

    @NotBlank(message = "field is required")
    private String password;

    @NotBlank(message = "field is required")
    private String role;

}
