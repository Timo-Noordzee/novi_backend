package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import com.timo_noordzee.novi.backend.dto.CreateEmployeeDto;
import com.timo_noordzee.novi.backend.dto.UpdateEmployeeDto;
import com.timo_noordzee.novi.backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("")
    public List<EmployeeEntity> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeEntity getById(@PathVariable("id") final String id) {
        return employeeService.getById(id);
    }

    @PostMapping("")
    public EmployeeEntity addEmployee(
            @Valid @RequestBody final CreateEmployeeDto createEmployeeDto
    ) {
        return employeeService.add(createEmployeeDto);
    }

    @PutMapping("/{id}")
    public EmployeeEntity updateEmployee(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateEmployeeDto updateEmployeeDto
    ) {
        return employeeService.update(id, updateEmployeeDto);
    }

    @DeleteMapping("/{id}")
    public EmployeeEntity deleteById(@PathVariable("id") final String id) {
        return employeeService.deleteById(id);
    }

}
