package com.timo_noordzee.novi.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timo_noordzee.novi.backend.data.*;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import com.timo_noordzee.novi.backend.repository.*;
import com.timo_noordzee.novi.backend.util.*;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InvoiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private RepairLineRepository repairLineRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private final CustomerTestUtils customerTestUtils = new CustomerTestUtils();
    private final VehicleTestUtils vehicleTestUtils = new VehicleTestUtils();
    private final RepairTestUtils repairTestUtils = new RepairTestUtils();
    private final RepairLineTestUtils repairLineTestUtils = new RepairLineTestUtils();
    private final InvoiceTestUtils invoiceTestUtils = new InvoiceTestUtils();

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(customerRepository).isNotNull();
        assertThat(vehicleRepository).isNotNull();
        assertThat(repairRepository).isNotNull();
        assertThat(repairLineRepository).isNotNull();
        assertThat(invoiceRepository).isNotNull();
    }

    @Test
    void addInvoiceWorksThroughAllLayers() throws Exception {
        final CustomerEntity customerEntity = customerTestUtils.generateMockEntity();
        customerRepository.save(customerEntity);
        final VehicleEntity vehicleEntity = vehicleTestUtils.generateMockEntity(customerEntity);
        vehicleRepository.save(vehicleEntity);
        final RepairEntity repairEntity = repairTestUtils.generateMockEntity(vehicleEntity);
        repairRepository.save(repairEntity);
        for (int i = 0; i < 10; i++) {
            final RepairLineEntity repairLineEntity = repairLineTestUtils.generateMockEntity(repairEntity);
            repairLineRepository.save(repairLineEntity);
        }
        final CreateInvoiceDto createInvoiceDto = invoiceTestUtils.generateMockCreateDto(repairEntity.getId().toString());
        final String payload = objectMapper.writeValueAsString(createInvoiceDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void getByIdAsPdfWorksThroughAllLayers() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        invoiceRepository.save(invoiceEntity);
        final String id = invoiceEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(invoiceEntity.getData()));
    }

    @Test
    void getByIdAsJsonWorksThroughAllLayers() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        invoiceRepository.save(invoiceEntity);
        final String id = invoiceEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/{id}", id)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(invoiceEntity.getId().toString())))
                .andExpect(jsonPath("$.status", Is.is(invoiceEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(invoiceEntity.getData()))));
    }

    @Test
    void getAllReturnsArrayWithoutDataField() throws Exception {
        final InvoiceEntity invoiceEntity1 = invoiceTestUtils.generateMockEntity();
        final InvoiceEntity invoiceEntity2 = invoiceTestUtils.generateMockEntity();
        invoiceRepository.save(invoiceEntity1);
        invoiceRepository.save(invoiceEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", Is.is(invoiceEntity1.getId().toString())))
                .andExpect(jsonPath("$[0].status", Is.is(invoiceEntity1.getStatus().getValue())))
                .andExpect(jsonPath("$[0].data").doesNotExist())
                .andExpect(jsonPath("$[1].id", Is.is(invoiceEntity2.getId().toString())))
                .andExpect(jsonPath("$[1].status", Is.is(invoiceEntity2.getStatus().getValue())))
                .andExpect(jsonPath("$[1].data").doesNotExist());
    }

    @Test
    void updateInvoiceWorksThroughAllLayers() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        invoiceRepository.save(invoiceEntity);
        final UpdateInvoiceDto updateInvoiceDto = invoiceTestUtils.generateMockUpdateDto();
        final String payload = objectMapper.writeValueAsString(updateInvoiceDto);
        final String id = invoiceEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/invoices/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(invoiceEntity.getId().toString())))
                .andExpect(jsonPath("$.status", Is.is(updateInvoiceDto.getStatus())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(invoiceEntity.getData()))));
    }

    @Test
    void deleteInvoiceWorksThroughAllLayers() throws Exception {
        final InvoiceEntity invoiceEntity = invoiceTestUtils.generateMockEntity();
        invoiceRepository.save(invoiceEntity);
        final String id = invoiceEntity.getId().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Is.is(invoiceEntity.getId().toString())))
                .andExpect(jsonPath("$.status", Is.is(invoiceEntity.getStatus().getValue())))
                .andExpect(jsonPath("$.data", Is.is(Base64.getEncoder().encodeToString(invoiceEntity.getData()))));
    }

}
