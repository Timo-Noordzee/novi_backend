package com.timo_noordzee.novi.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.InvoiceEntity;
import com.timo_noordzee.novi.backend.domain.InvoiceStatus;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.UnknownStatusException;
import com.timo_noordzee.novi.backend.service.InvoiceService;
import com.timo_noordzee.novi.backend.util.CustomerTestUtils;
import com.timo_noordzee.novi.backend.util.InvoiceTestUtils;
import com.timo_noordzee.novi.backend.util.VehicleTestUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvoiceService invoiceService;

    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final InvoiceTestUtils invoiceTestUtils = new InvoiceTestUtils();

    @Test
    void getAllReturnsListWithoutDataField() throws Exception {
        final List<InvoiceEntity> invoiceEntityList = new ArrayList<>();
        invoiceEntityList.add(invoiceTestUtils.generateMockEntity(null));
        invoiceEntityList.add(invoiceTestUtils.generateMockEntity(null));
        when(invoiceService.getAll()).thenReturn(invoiceEntityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(invoiceEntityList.get(0).getId().toString())))
                .andExpect(jsonPath("$[0].status", Is.is(invoiceEntityList.get(0).getStatus().getValue())))
                .andExpect(jsonPath("$[0].data").doesNotExist())
                .andExpect(jsonPath("$[0].id", Is.is(invoiceEntityList.get(0).getId().toString())))
                .andExpect(jsonPath("$[0].status", Is.is(invoiceEntityList.get(0).getStatus().getValue())))
                .andExpect(jsonPath("$[0].data").doesNotExist());
    }

    @Test
    void getByIdForNonexistentReturnsEntityNotFoundException() throws Exception {
        final String id = UUID.randomUUID().toString();
        when(invoiceService.getById(any(String.class))).thenThrow(new EntityNotFoundException(id, InvoiceEntity.class.getSimpleName()));

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", Is.is(EntityNotFoundException.ERROR_CODE)));
    }

    @Test
    void getByIdForWithoutAcceptHeaderReturnsPdf() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        when(invoiceService.getById(any(String.class))).thenReturn(invoiceEntity);
        final String id = invoiceEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(invoiceEntity.getData()));
    }

    @Test
    void getByIdWithJsonAcceptHeaderReturnsJson() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        when(invoiceService.getById(any(String.class))).thenReturn(invoiceEntity);
        final String id = invoiceEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/{id}", id)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(invoiceEntity.getId().toString())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(invoiceEntity.getData()))))
                .andExpect(jsonPath("$.status", Is.is(invoiceEntity.getStatus().getValue())));
    }

    @Test
    void addWithInvalidPayloadReturnsValidationErrors() throws Exception {
        final CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder().build();
        final String payload = objectMapper.writeValueAsString(createInvoiceDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.repairId", Is.is("field is required")));
    }

    @Test
    void addWithInvalidStatusReturnsUnknownStatusException() throws Exception {
        final String repairId = UUID.randomUUID().toString();
        final CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder().repairId(repairId).status(-1).build();
        final String payload = objectMapper.writeValueAsString(createInvoiceDto);
        when(invoiceService.add(any(CreateInvoiceDto.class))).thenThrow(new UnknownStatusException(String.valueOf(createInvoiceDto.getStatus())));

        mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", Is.is(UnknownStatusException.ERROR_CODE)));
    }

    @Test
    void addWithValidPayloadAndWithoutAcceptHeaderReturnsPdf() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        final CreateInvoiceDto createInvoiceDto = invoiceTestUtils.generateMockCreateDto(invoiceEntity.getId().toString());
        when(invoiceService.add(any(CreateInvoiceDto.class))).thenReturn(invoiceEntity);
        final String payload = objectMapper.writeValueAsString(createInvoiceDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(invoiceEntity.getData()))
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void addWithValidPayloadAndJsonAcceptHeaderHeaderReturnsJsonInvoiceEntity() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        final CreateInvoiceDto createInvoiceDto = invoiceTestUtils.generateMockCreateDto(invoiceEntity.getId().toString());
        when(invoiceService.add(any(CreateInvoiceDto.class))).thenReturn(invoiceEntity);
        final String payload = objectMapper.writeValueAsString(createInvoiceDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(invoiceEntity.getId().toString())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(invoiceEntity.getData()))))
                .andExpect(jsonPath("$.status", Is.is(invoiceEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void updateExistingInvoiceReturnsUpdatedInvoiceEntity() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        final UpdateInvoiceDto updateInvoiceDto = invoiceTestUtils.generateMockUpdateDto();
        when(invoiceService.update(any(String.class), any(UpdateInvoiceDto.class))).thenAnswer(i -> {
            final UpdateInvoiceDto update = i.getArgument(1);
            invoiceEntity.setStatus(InvoiceStatus.parse(update.getStatus()));
            invoiceEntity.setPaidAt(update.getPaidAt());
            return invoiceEntity;
        });
        final String id = invoiceEntity.getId().toString();
        final String payload = objectMapper.writeValueAsString(updateInvoiceDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/invoices/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(invoiceEntity.getId().toString())))
                .andExpect(jsonPath("$.status", Is.is(invoiceEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(invoiceEntity.getData()))))
                .andExpect(jsonPath("$.paidAt").isNotEmpty());
    }

    @Test
    void deleteExistingInvoiceReturnsDeletedInvoiceEntity() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        final String id = invoiceEntity.getId().toString();
        when(invoiceService.deleteById(any(String.class))).thenReturn(invoiceEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/{id}", id)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(invoiceEntity.getId().toString())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(invoiceEntity.getData()))))
                .andExpect(jsonPath("$.status", Is.is(invoiceEntity.getStatus().getValue())));
    }

}
