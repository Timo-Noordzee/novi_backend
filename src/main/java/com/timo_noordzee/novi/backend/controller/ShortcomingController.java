package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.ShortcomingEntity;
import com.timo_noordzee.novi.backend.dto.CreateShortcomingDto;
import com.timo_noordzee.novi.backend.dto.UpdateShortcomingDto;
import com.timo_noordzee.novi.backend.service.ShortcomingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/shortcomings")
@RequiredArgsConstructor
public class ShortcomingController {

    private final ShortcomingService shortcomingService;

    @GetMapping("")
    public List<ShortcomingEntity> getAll() {
        return shortcomingService.getAll();
    }

    @GetMapping("/{id}")
    public ShortcomingEntity getById(@PathVariable("id") final String id) {
        return shortcomingService.getById(id);
    }

    @PostMapping("")
    public ResponseEntity<ShortcomingEntity> addShortcoming(
            @Valid @RequestBody final CreateShortcomingDto createShortcomingDto
    ) {
        final ShortcomingEntity shortcomingEntity = shortcomingService.add(createShortcomingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(shortcomingEntity);
    }

    @PutMapping("/{id}")
    public ShortcomingEntity updateShortcoming(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateShortcomingDto updateShortcomingDto
    ) {
        return shortcomingService.update(id, updateShortcomingDto);
    }

    @DeleteMapping("/{id}")
    public ShortcomingEntity deleteById(@PathVariable("id") final String id) {
        return shortcomingService.deleteById(id);
    }

}
