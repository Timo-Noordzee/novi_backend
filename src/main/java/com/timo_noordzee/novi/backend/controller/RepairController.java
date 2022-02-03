package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.RepairLineEntity;
import com.timo_noordzee.novi.backend.dto.AddRepairLinesDto;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairLineDto;
import com.timo_noordzee.novi.backend.service.RepairService;
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
@RequestMapping("/repairs")
@RequiredArgsConstructor
@Tag(name = "Repair", description = "Endpoints for managing repairs and repair lines")
public class RepairController {

    private final RepairService repairService;

    @GetMapping("")
    @Operation(summary = "Get all Repairs")
    public List<RepairEntity> getAll() {
        return repairService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a Repair by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "3f6b0479-ef88-4f63-bd65-af3aa8ad5b72",
                            description = "ID of the Repair"
                    ))
            }
    )
    public RepairEntity getById(@PathVariable("id") final String id) {
        return repairService.getById(id);
    }

    @PostMapping("")
    @Operation(summary = "Add a new Repair")
    public ResponseEntity<RepairEntity> addRepair(
            @Valid @RequestBody final CreateRepairDto createRepairDto
    ) {
        final RepairEntity repairEntity = repairService.add(createRepairDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repairEntity);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Repair",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "0ec4abcc-f4df-4ef6-ae45-a71ecd05a316",
                            description = "ID of the Repair"
                    ))
            }
    )
    public RepairEntity updateRepair(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateRepairDto updateRepairDto
    ) {
        return repairService.update(id, updateRepairDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Repair",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "0ec4abcc-f4df-4ef6-ae45-a71ecd05a316",
                            description = "ID of the Repair"
                    ))
            }
    )
    public RepairEntity deleteById(@PathVariable("id") final String id) {
        return repairService.deleteById(id);
    }

    @PostMapping("/{id}/lines")
    @Operation(
            summary = "Add a line to a Repair",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "0ec4abcc-f4df-4ef6-ae45-a71ecd05a316",
                            description = "ID of the Repair"
                    ))
            }
    )
    public RepairEntity addLinesToRepair(
            @PathVariable("id") final String id,
            @Valid @RequestBody final AddRepairLinesDto addRepairLinesDto
    ) {
        repairService.addLinesToRepair(id, addRepairLinesDto);
        return repairService.getById(id);
    }

    @PutMapping("/{id}/lines/{lineId}")
    @Operation(
            summary = "Update a Repair line",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "0ec4abcc-f4df-4ef6-ae45-a71ecd05a316",
                            description = "ID of the Repair"
                    )),
                    @Parameter(name = "lineId", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "f5161f7a-427e-46e5-8a60-4825c88584bc",
                            description = "Id of the Repair line"
                    ))
            }
    )
    public RepairLineEntity updateRepairLine(
            @PathVariable("id") final String id,
            @PathVariable("lineId") final String lineId,
            @Valid @RequestBody final UpdateRepairLineDto updateRepairLineDto
    ) {
        return repairService.updateRepairLine(id, lineId, updateRepairLineDto);
    }

    @DeleteMapping("/{id}/lines/{lineId}")
    @Operation(
            summary = "Delete a Repair line",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "0ec4abcc-f4df-4ef6-ae45-a71ecd05a316",
                            description = "ID of the Repair"
                    )),
                    @Parameter(name = "lineId", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "f5161f7a-427e-46e5-8a60-4825c88584bc",
                            description = "Id of the Repair line"
                    ))
            }
    )
    public RepairLineEntity updateRepairLine(
            @PathVariable("id") final String id,
            @PathVariable("lineId") final String lineId
    ) {
        return repairService.deleteRepairLine(id, lineId);
    }

}