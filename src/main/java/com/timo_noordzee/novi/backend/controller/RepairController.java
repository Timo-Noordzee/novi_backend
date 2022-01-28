package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.RepairLineEntity;
import com.timo_noordzee.novi.backend.dto.AddRepairLinesDto;
import com.timo_noordzee.novi.backend.dto.CreateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairDto;
import com.timo_noordzee.novi.backend.dto.UpdateRepairLineDto;
import com.timo_noordzee.novi.backend.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;

    @GetMapping("")
    public List<RepairEntity> getAll() {
        return repairService.getAll();
    }

    @GetMapping("/{id}")
    public RepairEntity getById(@PathVariable("id") final String id) {
        return repairService.getById(id);
    }

    @PostMapping("")
    public ResponseEntity<RepairEntity> addRepair(
            @Valid @RequestBody final CreateRepairDto createRepairDto
    ) {
        final RepairEntity repairEntity = repairService.add(createRepairDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repairEntity);
    }

    @PutMapping("/{id}")
    public RepairEntity updateRepair(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateRepairDto updateRepairDto
    ) {
        return repairService.update(id, updateRepairDto);
    }

    @DeleteMapping("/{id}")
    public RepairEntity deleteById(@PathVariable("id") final String id) {
        return repairService.deleteById(id);
    }

    @PostMapping("/{id}/lines")
    public RepairEntity addLinesToRepair(
            @PathVariable("id") final String id,
            @Valid @RequestBody final AddRepairLinesDto addRepairLinesDto
    ) {
        repairService.addLinesToRepair(id, addRepairLinesDto);
        return repairService.getById(id);
    }

    @PutMapping("/{id}/lines/{lineId}")
    public RepairLineEntity updateRepairLine(
            @PathVariable("id") final String id,
            @PathVariable("lineId") final String lineId,
            @Valid @RequestBody final UpdateRepairLineDto updateRepairLineDto
    ) {
        return repairService.updateRepairLine(id, lineId, updateRepairLineDto);
    }

    @DeleteMapping("/{id}/lines/{lineId}")
    public RepairLineEntity updateRepairLine(
            @PathVariable("id") final String id,
            @PathVariable("lineId") final String lineId
    ) {
        return repairService.deleteRepairLine(id, lineId);
    }

}