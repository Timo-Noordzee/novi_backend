package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class CreateRepairDto implements CreateDto {

    @Schema(example = "21bdc260-d585-41e8-950d-56f5ea9208cc", format = "uuid")
    private String id;

    @Schema(example = "De klant is niet akkoord gegaan met de reparatie, omdat de kosten te hoog zijn")
    private String remarks;

    @Schema(example = "1", allowableValues = {"0", "1", "2", "3"})
    private int status;

    @Schema(example = "1FTPW14V87FA46384", format = "vin", externalDocs = @ExternalDocumentation(
            url = "https://www.iso.org/standard/52200.html",
            description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
    ))
    @NotEmpty(message = "field is required")
    private String vehicleId;

}
