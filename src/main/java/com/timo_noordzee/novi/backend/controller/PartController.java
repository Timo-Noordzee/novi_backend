package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.dto.CreatePartDto;
import com.timo_noordzee.novi.backend.dto.UpdatePartDto;
import com.timo_noordzee.novi.backend.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @GetMapping("")
    public List<PartEntity> getAll() {
        return partService.getAll();
    }

    @GetMapping("/{id}")
    public PartEntity getById(@PathVariable("id") final String id) {
        return partService.getById(id);
    }

    @PostMapping("")
    public ResponseEntity<PartEntity> addPart(
            @Valid @RequestBody final CreatePartDto createPartDto
    ) {
        final PartEntity partEntity = partService.add(createPartDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(partEntity);
    }

    @PutMapping("/{id}")
    public PartEntity updatePart(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdatePartDto updatePartDto
    ) {
        return partService.update(id, updatePartDto);
    }

    @DeleteMapping("/{id}")
    public PartEntity deleteById(@PathVariable("id") final String id) {
        return partService.deleteById(id);
    }

}
