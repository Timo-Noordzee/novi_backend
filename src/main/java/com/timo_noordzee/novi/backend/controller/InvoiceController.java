package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.InvoiceEntity;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import com.timo_noordzee.novi.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("")
    public List<InvoiceEntity> getAll() {
        return invoiceService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable("id") final String id,
            @RequestHeader(value = "Accept", defaultValue = "application/pdf") final String acceptHeader
    ) {
        final InvoiceEntity invoiceEntity = invoiceService.getById(id);

        if (StringUtils.equals(acceptHeader, MediaType.APPLICATION_JSON_VALUE)) {
            return ResponseEntity.ok(invoiceEntity);
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentLength(invoiceEntity.getData().length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(invoiceEntity.getData());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addInvoice(
            @Valid @RequestBody final CreateInvoiceDto createInvoiceDto,
            @RequestHeader(value = "Accept") final String acceptHeader
    ) {
        final InvoiceEntity invoiceEntity = invoiceService.add(createInvoiceDto);
        final URI uri = MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(InvoiceController.class)
                .getById(invoiceEntity.getId().toString(), MediaType.APPLICATION_PDF_VALUE)).buildAndExpand().toUri();

        if (StringUtils.equals(acceptHeader, MediaType.APPLICATION_JSON_VALUE)) {
            return ResponseEntity.created(uri).contentType(MediaType.APPLICATION_JSON).body(invoiceEntity);
        } else {
            final byte[] data = invoiceEntity.getData();
            return ResponseEntity.created(uri).contentLength(data.length).contentType(MediaType.APPLICATION_PDF).body(data);
        }
    }

    @PutMapping("/{id}")
    public InvoiceEntity updateInvoice(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateInvoiceDto updateInvoiceDto
    ) {
        return invoiceService.update(id, updateInvoiceDto);
    }

    @DeleteMapping("/{id}")
    public InvoiceEntity deleteById(@PathVariable("id") final String id) {
        return invoiceService.deleteById(id);
    }

}