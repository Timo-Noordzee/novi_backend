package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CreateEmployeeDto implements CreateDto {

    private String id;

    @NotBlank(message = "field is required")
    private String name;

    @NotBlank(message = "field is required")
    private String surname;

    @NotBlank(message = "field is required")
    private String email;

    @NotBlank(message = "field is required")
    private String password;

    @NotBlank(message = "field is required")
    private String role;

}
