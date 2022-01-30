package com.timo_noordzee.novi.backend.dto;

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
    private List<Part> parts;

    @Valid
    private List<Action> actions;

    @Valid
    private List<Custom> custom;


    @Data
    @Builder
    public static class Part {

        @NotNull(message = "field is required")
        private String id;

        @Min(value = 0, message = "the minimum value is 1")
        public int amount;

        public int getAmount() {
            return amount == 0 ? 1 : amount;
        }
    }

    @Data
    @Builder
    public static class Action {

        @NotNull(message = "field is required")
        private String id;

        @Min(value = 0, message = "the minimum value is 1")
        private int amount;

        public int getAmount() {
            return amount == 0 ? 1 : amount;
        }
    }

    @Data
    @Builder
    public static class Custom {

        @NotBlank(message = "field is required")
        private String name;

        @Min(value = 0, message = "the minimum value is 1")
        private int amount;

        @NotNull(message = "field is required")
        @Min(value = 0, message = "the minimum value is 0")
        private Double price;

        @NotNull(message = "field is required")
        private Integer type;

        public int getAmount() {
            return amount == 0 ? 1 : amount;
        }
    }

}
