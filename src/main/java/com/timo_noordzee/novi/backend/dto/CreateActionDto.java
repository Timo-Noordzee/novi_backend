package com.timo_noordzee.novi.backend.dto;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateActionDto implements CreateDto {

    @Schema(example = "fc6cbf76-837a-4ea6-a20b-0e22f817e55a", format = "uuid")
    private String id;

    @Schema(example = "Ruitenwisser vervangen")
    @NotBlank(message = "field is required")
    private String name;

    @Schema(example = "Jansen")
    @NotNull(message = "field is required")
    @Min(value = 0, message = "the minimum value is 0")
    private Double price;

}
