package com.timo_noordzee.novi.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeeDto {

    private String name;

    private String surname;

    private String password;

    private String role;

}
