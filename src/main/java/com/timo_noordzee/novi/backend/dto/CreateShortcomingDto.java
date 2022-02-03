package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CreateShortcomingDto implements CreateDto {

    @Schema(example = "e3604bc3-8859-4e6e-874a-316370d33f5d", format = "uuid")
    private String id;

    @Schema(example = "De remschijven zijn versleten")
    @NotBlank(message = "field is required")
    private String description;

    @Schema(example = "1FTPW14V87FA46384", format = "vin", externalDocs = @ExternalDocumentation(
            url = "https://www.iso.org/standard/52200.html",
            description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
    ))
    @NotBlank(message = "field is required")
    private String vehicleId;

}
