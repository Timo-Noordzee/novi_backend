package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.VehicleEntity;
import com.timo_noordzee.novi.backend.dto.CreateVehicleDto;
import com.timo_noordzee.novi.backend.dto.UpdateVehicleDto;
import com.timo_noordzee.novi.backend.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("")
    public List<VehicleEntity> getAll() {
        return vehicleService.getAll();
    }

    @GetMapping("/{id}")
    public VehicleEntity getById(@PathVariable("id") final String id) {
        return vehicleService.getById(id);
    }

    @PostMapping("")
    public ResponseEntity<VehicleEntity> addVehicle(
            @Valid @RequestBody final CreateVehicleDto createVehicleDto
    ) {
        final VehicleEntity vehicleEntity = vehicleService.add(createVehicleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleEntity);
    }

    @PutMapping("/{id}")
    public VehicleEntity updateVehicle(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateVehicleDto updateVehicleDto
    ) {
        return vehicleService.update(id, updateVehicleDto);
    }

    @DeleteMapping("/{id}")
    public VehicleEntity deleteById(@PathVariable("id") final String id) {
        return vehicleService.deleteById(id);
    }

}
