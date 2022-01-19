package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.dto.UpdateCustomerDto;
import com.timo_noordzee.novi.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("")
    public List<CustomerEntity> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    public CustomerEntity getById(@PathVariable("id") final String id) {
        return customerService.getById(id);
    }

    @PostMapping("")
    public ResponseEntity<CustomerEntity> addCustomer(
            @Valid @RequestBody final CreateCustomerDto createCustomerDto
    ) {
        final CustomerEntity customerEntity = customerService.add(createCustomerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerEntity);
    }

    @PutMapping("/{id}")
    public CustomerEntity updateCustomer(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateCustomerDto updateCustomerDto
    ) {
        return customerService.update(id, updateCustomerDto);
    }

    @DeleteMapping("/{id}")
    public CustomerEntity deleteById(@PathVariable("id") final String id) {
        return customerService.deleteById(id);
    }

}