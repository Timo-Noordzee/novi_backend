package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import com.timo_noordzee.novi.backend.service.VehiclePapersService;
import com.timo_noordzee.novi.backend.service.VehicleService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle", description = "Endpoints for managing vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehiclePapersService vehiclePapersService;

    @GetMapping("")
    @Operation(summary = "Get all Vehicles")
    public List<VehicleEntity> getAll() {
        return vehicleService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a Vehicle by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "SAJWA2GZ0F8V79011",
                            description = "ID of the Vehicle",
                            format = "vin",
                            externalDocs = @ExternalDocumentation(
                                    url = "https://www.iso.org/standard/52200.html",
                                    description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
                            )
                    ))
            }
    )
    public VehicleEntity getById(@PathVariable("id") final String id) {
        return vehicleService.getById(id);
    }

    @PostMapping("")
    @Operation(summary = "Add a new Vehicle")
    public ResponseEntity<VehicleEntity> addVehicle(
            @Valid @RequestBody final CreateVehicleDto createVehicleDto
    ) {
        final VehicleEntity vehicleEntity = vehicleService.add(createVehicleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleEntity);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Vehicle",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "SAJWA2GZ0F8V79011",
                            description = "ID of the Vehicle",
                            format = "vin",
                            externalDocs = @ExternalDocumentation(
                                    url = "https://www.iso.org/standard/52200.html",
                                    description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
                            )
                    ))
            }
    )
    public VehicleEntity updateVehicle(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateVehicleDto updateVehicleDto
    ) {
        return vehicleService.update(id, updateVehicleDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Vehicle",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "SAJWA2GZ0F8V79011",
                            description = "ID of the Vehicle",
                            format = "vin",
                            externalDocs = @ExternalDocumentation(
                                    url = "https://www.iso.org/standard/52200.html",
                                    description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
                            )
                    ))
            }
    )
    public VehicleEntity deleteById(@PathVariable("id") final String id) {
        return vehicleService.deleteById(id);
    }

    @PostMapping("/{id}/papers")
    @Operation(
            summary = "Add a file to a Vehicle",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "SAJWA2GZ0F8V79011",
                            description = "ID of the Vehicle",
                            format = "vin",
                            externalDocs = @ExternalDocumentation(
                                    url = "https://www.iso.org/standard/52200.html",
                                    description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
                            )
                    ))
            }
    )
    public ResponseEntity<VehiclePapersEntity> addVehiclePapers(
            @PathVariable("id") final String id,
            @RequestParam("file") final MultipartFile file
    ) {
        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersService.add(id, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiclePapersEntity);
    }

    @GetMapping("/{id}/papers")
    @Operation(
            summary = "Get files for a Vehicle",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "SAJWA2GZ0F8V79011",
                            description = "ID of the Vehicle",
                            format = "vin",
                            externalDocs = @ExternalDocumentation(
                                    url = "https://www.iso.org/standard/52200.html",
                                    description = "ISO 3779:2009 Road vehicles — Vehicle identification number"
                            )
                    ))
            }
    )
    public List<VehiclePapersEntity> getPapersForVehicle(
            @PathVariable final String id
    ) {
        return vehiclePapersService.findAllForVehicle(id);
    }

}
