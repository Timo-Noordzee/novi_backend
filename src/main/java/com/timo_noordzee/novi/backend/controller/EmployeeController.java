package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import com.timo_noordzee.novi.backend.dto.CreateEmployeeDto;
import com.timo_noordzee.novi.backend.dto.UpdateEmployeeDto;
import com.timo_noordzee.novi.backend.service.EmployeeService;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Endpoints for managing `Employee`'s")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("")
    @Operation(summary = "Get all Employees")
    public List<EmployeeEntity> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get an Employee by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "41842996-06ee-4c3c-990f-376a47c2342e",
                            description = "ID of the Employee"
                    ))
            }
    )
    public EmployeeEntity getById(@PathVariable("id") final String id) {
        return employeeService.getById(id);
    }

    @PostMapping("")
    @Operation(summary = "Add a new Employee")
    public ResponseEntity<EmployeeEntity> addEmployee(
            @Valid @RequestBody final CreateEmployeeDto createEmployeeDto
    ) {
        final EmployeeEntity employeeEntity = employeeService.add(createEmployeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeEntity);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an Employee",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "41842996-06ee-4c3c-990f-376a47c2342e",
                            description = "ID of the Employee"
                    ))
            }
    )
    public EmployeeEntity updateEmployee(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateEmployeeDto updateEmployeeDto
    ) {
        return employeeService.update(id, updateEmployeeDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an Employee",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "41842996-06ee-4c3c-990f-376a47c2342e",
                            description = "ID of the Employee"
                    ))
            }
    )
    public EmployeeEntity deleteById(@PathVariable("id") final String id) {
        return employeeService.deleteById(id);
    }

}
