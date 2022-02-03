package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import com.timo_noordzee.novi.backend.service.ShortcomingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/shortcomings")
@RequiredArgsConstructor
@Tag(name = "Shortcoming", description = "Endpoints for managing `Vehicle` shortcoming")
public class ShortcomingController {

    private final ShortcomingService shortcomingService;

    @GetMapping("")
    @Operation(summary = "Get all Shortcomings")
    public List<ShortcomingEntity> getAll() {
        return shortcomingService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a Shortcoming by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "b929d3b2-b6e6-4930-96f8-167f32338cc1",
                            description = "ID of the Shortcoming"
                    ))
            }
    )
    public ShortcomingEntity getById(@PathVariable("id") final String id) {
        return shortcomingService.getById(id);
    }

    @PostMapping("")
    @Operation(summary = "Add a new Shortcoming")
    public ResponseEntity<ShortcomingEntity> addShortcoming(
            @Valid @RequestBody final CreateShortcomingDto createShortcomingDto
    ) {
        final ShortcomingEntity shortcomingEntity = shortcomingService.add(createShortcomingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(shortcomingEntity);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Shortcoming",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "b929d3b2-b6e6-4930-96f8-167f32338cc1",
                            description = "ID of the Shortcoming"
                    ))
            }
    )
    public ShortcomingEntity updateShortcoming(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateShortcomingDto updateShortcomingDto
    ) {
        return shortcomingService.update(id, updateShortcomingDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Shortcoming",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "b929d3b2-b6e6-4930-96f8-167f32338cc1",
                            description = "ID of the Shortcoming"
                    ))
            }
    )
    public ShortcomingEntity deleteById(@PathVariable("id") final String id) {
        return shortcomingService.deleteById(id);
    }

}
