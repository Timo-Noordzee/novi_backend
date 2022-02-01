package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.data.InvoiceEntity;
import com.timo_noordzee.novi.backend.data.RepairEntity;
import com.timo_noordzee.novi.backend.data.RepairLineEntity;
import com.timo_noordzee.novi.backend.domain.RepairLineType;
import com.timo_noordzee.novi.backend.dto.CreateInvoiceDto;
import com.timo_noordzee.novi.backend.dto.UpdateInvoiceDto;
import com.timo_noordzee.novi.backend.exception.GenerateInvoiceException;
import com.timo_noordzee.novi.backend.mapper.InvoiceMapper;
import com.timo_noordzee.novi.backend.projection.InvoiceWithoutDataProjection;
import com.timo_noordzee.novi.backend.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceService extends BaseRestService<InvoiceEntity, UUID, CreateInvoiceDto, UpdateInvoiceDto, InvoiceRepository, InvoiceMapper> {

    private final RepairService repairService;

    public InvoiceService(final InvoiceRepository repository, final InvoiceMapper mapper, final RepairService repairService) {
        super(repository, mapper);
        this.repairService = repairService;
    }

    @Override
    UUID parseId(final String id) {
        return parseUUID(id);
    }

    @Override
    String entityType() {
        return InvoiceEntity.class.getSimpleName();
    }

    @Override
    protected InvoiceEntity fromCreateDto(final CreateInvoiceDto createDto) {
        final InvoiceEntity invoiceEntity = mapper.fromCreateDto(createDto);
        final RepairEntity repairEntity = repairService.getById(createDto.getRepairId());
        final byte[] data = generateInvoicePdf(repairEntity);
        invoiceEntity.setData(data);
        return invoiceEntity;
    }

    @Override
    protected List<InvoiceEntity> findAll() {
        return repository.findAllProjectedBy().stream().map(this::convertProjectionToEntity).collect(Collectors.toList());
    }

    private InvoiceEntity convertProjectionToEntity(final InvoiceWithoutDataProjection projection) {
        return InvoiceEntity.builder()
                .id(projection.getId())
                .createdAt(projection.getCreatedAt())
                .paidAt(projection.getPaidAt())
                .status(projection.getStatus())
                .build();
    }

    private byte[] generateInvoicePdf(final RepairEntity repairEntity) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setSuffix(".html");
            templateResolver.setPrefix("templates/");
            templateResolver.setTemplateMode(TemplateMode.HTML);

            final TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            final List<RepairLineEntity> partLines = repairEntity.getLines().stream()
                    .filter(line -> line.getType() == RepairLineType.PART)
                    .collect(Collectors.toList());

            final List<RepairLineEntity> actionLines = repairEntity.getLines().stream()
                    .filter(line -> line.getType() == RepairLineType.ACTION)
                    .collect(Collectors.toList());

            final double total = repairEntity.getLines().stream().map(RepairLineEntity::getTotalPrice).reduce(0.0, Double::sum);

            final Context context = new Context();
            context.setVariable("id", repairEntity.getId().toString());
            context.setVariable("createdAt", new Date());
            context.setVariable("vehicle", repairEntity.getVehicle());
            context.setVariable("customer", repairEntity.getVehicle().getOwner());
            context.setVariable("parts", partLines);
            context.setVariable("actions", actionLines);
            context.setVariable("total", total);

            final String html = templateEngine.process("invoice_template", context);

            final ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (final Exception exception) {
            throw new GenerateInvoiceException(exception.getMessage());
        }
    }
}
