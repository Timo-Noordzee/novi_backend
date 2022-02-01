package com.timo_noordzee.novi.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UpdateInvoiceDto {

    private int status;

    private Date paidAt;

}
