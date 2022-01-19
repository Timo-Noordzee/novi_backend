package com.timo_noordzee.novi.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEmployeeDto {

    private String name;

    private String surname;

    private String password;

    private String role;

}
