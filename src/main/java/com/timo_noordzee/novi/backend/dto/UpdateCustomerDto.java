package com.timo_noordzee.novi.backend.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UpdateCustomerDto {

    private String name;

    private String surname;

    @Email(message = "isn't a valid email address")
    private String email;

    private String phone;

}
