package com.timo_noordzee.novi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UpdateInvoiceDto {

    @Schema(example = "1", allowableValues = {"1", "2"})
    private int status;

    @Schema(example = "2022-01-10T12:08:44.000", format = "date-time")
    private Date paidAt;

}
