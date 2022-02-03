package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.dto.UpdateCustomerDto;
import com.timo_noordzee.novi.backend.service.CustomerService;
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
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Endpoints for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("")
    @Operation(summary = "Get all Customers")
    public List<CustomerEntity> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Customer by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "f1c1ed01-6a3d-41f8-8054-ecdaf23dbecf",
                            description = "ID of the Customer"
                    ))
            }
    )
    public CustomerEntity getById(@PathVariable("id") final String id) {
        return customerService.getById(id);
    }

    @PostMapping("")
    @Operation(summary = "Add a new Customer")
    public ResponseEntity<CustomerEntity> addCustomer(
            @Valid @RequestBody final CreateCustomerDto createCustomerDto
    ) {
        final CustomerEntity customerEntity = customerService.add(createCustomerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerEntity);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Customer",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "f1c1ed01-6a3d-41f8-8054-ecdaf23dbecf",
                            description = "ID of the Customer"
                    ))
            }
    )
    public CustomerEntity updateCustomer(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateCustomerDto updateCustomerDto
    ) {
        return customerService.update(id, updateCustomerDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Customer",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "f1c1ed01-6a3d-41f8-8054-ecdaf23dbecf",
                            description = "ID of the Customer"
                    ))
            }
    )
    public CustomerEntity deleteById(@PathVariable("id") final String id) {
        return customerService.deleteById(id);
    }

}