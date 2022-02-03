package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.service.VehiclePapersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiclePapers")
@RequiredArgsConstructor
@Tag(name = "VehiclePapers", description = "Endpoints for managing Vehicle papers")
public class VehiclePapersController {

    private final VehiclePapersService vehiclePapersService;

    @GetMapping("")
    @Operation(summary = "Get all Vehicle papers")
    public List<VehiclePapersEntity> getAll() {
        return vehiclePapersService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Vehicle papers by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "0b87c428-4750-43c8-a281-c6703f9e6dda",
                            description = "ID of the Vehicle papers"
                    ))
            }
    )
    public ResponseEntity<byte[]> getFile(@PathVariable final String id) {
        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersService.getById(id);

        return ResponseEntity.ok()
                .contentLength(vehiclePapersEntity.getData().length)
                .contentType(MediaType.valueOf(vehiclePapersEntity.getType()))
                .body(vehiclePapersEntity.getData());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Vehicle papers",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "0b87c428-4750-43c8-a281-c6703f9e6dda",
                            description = "ID of the Vehicle papers"
                    ))
            }
    )
    public VehiclePapersEntity deleteById(@PathVariable() final String id) {
        return vehiclePapersService.deleteById(id);
    }

}
