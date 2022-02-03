package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.dto.CreatePartDto;
import com.timo_noordzee.novi.backend.dto.UpdatePartDto;
import com.timo_noordzee.novi.backend.service.PartService;
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
@RequestMapping("/parts")
@RequiredArgsConstructor
@Tag(name = "Part", description = "Endpoints for managing parts")
public class PartController {

    private final PartService partService;

    @GetMapping("")
    @Operation(summary = "Get all Parts")
    public List<PartEntity> getAll() {
        return partService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a Part by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "56c001d1-c169-4f88-9816-693b4927472b",
                            description = "ID of the Part"
                    ))
            }
    )
    public PartEntity getById(@PathVariable("id") final String id) {
        return partService.getById(id);
    }

    @PostMapping("")
    @Operation(summary = "Add a new Part")
    public ResponseEntity<PartEntity> addPart(
            @Valid @RequestBody final CreatePartDto createPartDto
    ) {
        final PartEntity partEntity = partService.add(createPartDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(partEntity);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Part",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "56c001d1-c169-4f88-9816-693b4927472b",
                            description = "ID of the Part"
                    ))
            }
    )
    public PartEntity updatePart(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdatePartDto updatePartDto
    ) {
        return partService.update(id, updatePartDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Part",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "56c001d1-c169-4f88-9816-693b4927472b",
                            description = "ID of the Part"
                    ))
            }
    )
    public PartEntity deleteById(@PathVariable("id") final String id) {
        return partService.deleteById(id);
    }

}
