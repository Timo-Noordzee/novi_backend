package com.timo_noordzee.novi.backend.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class AddRepairLinesDto {

    @Valid
    @ArraySchema(schema = @Schema(implementation = Part.class))
    private List<Part> parts;

    @Valid
    @ArraySchema(schema = @Schema(implementation = Action.class))
    private List<Action> actions;

    @Valid
    @ArraySchema(schema = @Schema(implementation = Custom.class))
    private List<Custom> custom;


    @Data
    @Builder
    public static class Part {

        @NotNull(message = "field is required")
        @Schema(example = "f71f2846-4e53-45ac-b8c0-6edb93728698", format = "uuid")
        private String id;

        @Min(value = 0, message = "the minimum value is 1")
        @Schema(example = "1")
        public int amount;

        @Schema(hidden = true)
        public int getAmount() {
            return amount == 0 ? 1 : amount;
        }
    }

    @Data
    @Builder
    public static class Action {

        @Schema(example = "dcfca82f-c4b2-4b5d-ba42-7ac1729ef4df", format = "uuid")
        @NotNull(message = "field is required")
        private String id;

        @Min(value = 0, message = "the minimum value is 1")
        @Schema(example = "1")
        private int amount;

        @Schema(hidden = true)
        public int getAmount() {
            return amount == 0 ? 1 : amount;
        }
    }

    @Data
    @Builder
    public static class Custom {

        @NotBlank(message = "field is required")
        @Schema(example = "Software updaten")
        private String name;

        @Min(value = 0, message = "the minimum value is 1")
        @Schema(example = "1")
        private int amount;

        @NotNull(message = "field is required")
        @Min(value = 0, message = "the minimum value is 0")
        @Schema(example = "29.99", format = "double")
        private Double price;

        @NotNull(message = "field is required")
        @Schema(example = "1", allowableValues = {"0", "1"})
        private Integer type;

        @Schema(hidden = true)
        public int getAmount() {
            return amount == 0 ? 1 : amount;
        }
    }

}
