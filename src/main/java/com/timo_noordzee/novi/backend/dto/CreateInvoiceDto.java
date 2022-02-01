package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Builder
public class CreateInvoiceDto implements CreateDto {

    private String id;

    private Date paidAt;

    private int status;

    @NotEmpty(message = "field is required")
    private String repairId;

}
