package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import com.timo_noordzee.novi.backend.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    @GetMapping("")
    public List<ActionEntity> getAll() {
        return actionService.getAll();
    }

    @GetMapping("/{id}")
    public ActionEntity getById(@PathVariable("id") final String id) {
        return actionService.getById(id);
    }

    @PostMapping("")
    public ResponseEntity<ActionEntity> addAction(
            @Valid @RequestBody final CreateActionDto createActionDto
    ) {
        final ActionEntity actionEntity = actionService.add(createActionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(actionEntity);
    }

    @PutMapping("/{id}")
    public ActionEntity updateAction(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateActionDto updateActionDto
    ) {
        return actionService.update(id, updateActionDto);
    }

    @DeleteMapping("/{id}")
    public ActionEntity deleteById(@PathVariable("id") final String id) {
        return actionService.deleteById(id);
    }

}