package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Builder
public class CreateInvoiceDto implements CreateDto {

    @Schema(example = "6f563bf0-e1ab-4e63-b721-3d2c47b28963", format = "uuid")
    private String id;

    @Schema(example = "2022-01-10T12:08:44.000", format = "date-time")
    private Date paidAt;

    @Schema(example = "1", allowableValues = {"1", "2"})
    private int status;

    @Schema(example = "21bdc260-d585-41e8-950d-56f5ea9208cc", format = "uuid")
    @NotEmpty(message = "field is required")
    private String repairId;

}
