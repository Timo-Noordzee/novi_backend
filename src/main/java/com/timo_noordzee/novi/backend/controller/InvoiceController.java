package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.data.InvoiceEntity;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import com.timo_noordzee.novi.backend.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Invoice", description = "Endpoints for managing invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("")
    @Operation(summary = "Get all Invoices")
    public List<InvoiceEntity> getAll() {
        return invoiceService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get an Invoice by ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "3f6b0479-ef88-4f63-bd65-af3aa8ad5b72",
                            description = "ID of the Invoice"
                    ))
            }
    )
    public ResponseEntity<?> getById(
            @PathVariable("id") final String id,
            @RequestHeader(value = "Accept", required = false, defaultValue = "application/pdf") final String acceptHeader
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
    @Operation(summary = "Add a new Invoice")
    public ResponseEntity<?> addInvoice(
            @Valid @RequestBody final CreateInvoiceDto createInvoiceDto,
            @RequestHeader(value = "Accept", required = false, defaultValue = "application/pdf") final String acceptHeader
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
    @Operation(
            summary = "Update an Invoice",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "3f6b0479-ef88-4f63-bd65-af3aa8ad5b72",
                            description = "ID of the Invoice"
                    ))
            }
    )
    public InvoiceEntity updateInvoice(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateInvoiceDto updateInvoiceDto
    ) {
        return invoiceService.update(id, updateInvoiceDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an Invoice",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(
                            type = "string",
                            example = "3f6b0479-ef88-4f63-bd65-af3aa8ad5b72",
                            description = "ID of the Invoice"
                    ))
            }
    )
    public InvoiceEntity deleteById(@PathVariable("id") final String id) {
        return invoiceService.deleteById(id);
    }

}