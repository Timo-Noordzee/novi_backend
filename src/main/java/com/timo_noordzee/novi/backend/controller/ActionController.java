package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import com.timo_noordzee.novi.backend.service.ActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/actions")
@RequiredArgsConstructor
@Tag(name = "Action", description = "Endpoints for managing actions")
public class ActionController {

    private final ActionService actionService;

    @GetMapping("")
    @Operation(
            summary = "Get all Actions",
            responses = {@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = ActionEntity.class))
            ))}
    )
    public List<ActionEntity> getAll() {
        return actionService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get an Action by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "869f7f98-11b7-480c-adb1-4fbadee49edc",
                            description = "ID of the Action"
                    ))
            }
    )
    public ActionEntity getById(@PathVariable("id") final String id) {
        return actionService.getById(id);
    }

    @PostMapping("")
    @Operation(summary = "Add a new Action")
    public ResponseEntity<ActionEntity> addAction(
            @Valid @RequestBody final CreateActionDto createActionDto
    ) {
        final ActionEntity actionEntity = actionService.add(createActionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(actionEntity);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an Action",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "869f7f98-11b7-480c-adb1-4fbadee49edc",
                            description = "ID of the Action"
                    ))
            }
    )
    public ActionEntity updateAction(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateActionDto updateActionDto
    ) {
        return actionService.update(id, updateActionDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an Action",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "869f7f98-11b7-480c-adb1-4fbadee49edc",
                            description = "ID of the Action"
                    ))
            }
    )
    public ActionEntity deleteById(@PathVariable("id") final String id) {
        return actionService.deleteById(id);
    }

}