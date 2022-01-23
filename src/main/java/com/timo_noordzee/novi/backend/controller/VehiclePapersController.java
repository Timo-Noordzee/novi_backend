package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.VehiclePapersEntity;
import com.timo_noordzee.novi.backend.service.VehiclePapersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiclePapers")
@RequiredArgsConstructor
public class VehiclePapersController {

    private final VehiclePapersService vehiclePapersService;

    @GetMapping("")
    public List<VehiclePapersEntity> getAll() {
        return vehiclePapersService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable final String id) {
        final VehiclePapersEntity vehiclePapersEntity = vehiclePapersService.getById(id);

        return ResponseEntity.ok()
                .contentLength(vehiclePapersEntity.getData().length)
                .contentType(MediaType.valueOf(vehiclePapersEntity.getType()))
                .body(vehiclePapersEntity.getData());
    }

    @DeleteMapping("/{id}")
    public VehiclePapersEntity deleteById(@PathVariable() final String id) {
        return vehiclePapersService.deleteById(id);
    }

}
