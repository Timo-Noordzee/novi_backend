package com.timo_noordzee.novi.backend.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.timo_noordzee.novi.backend.data.CustomerEntity;
import com.timo_noordzee.novi.backend.dto.CreateCustomerDto;
import com.timo_noordzee.novi.backend.dto.UpdateCustomerDto;
import com.timo_noordzee.novi.backend.exception.EmailTakenException;
import com.timo_noordzee.novi.backend.mapper.CustomerMapper;
import com.timo_noordzee.novi.backend.repository.CustomerRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService extends BaseRestService<CustomerEntity, UUID, CreateCustomerDto, UpdateCustomerDto, CustomerRepository, CustomerMapper> {

    public CustomerService(final CustomerRepository repository, final CustomerMapper mapper) {
        super(repository, mapper);
    }

    @Override
    UUID parseId(final String id) {
        return parseUUID(id);
    }

    @Override
    String entityType() {
        return CustomerEntity.class.getSimpleName();
    }

    @Override
    protected Optional<CustomerEntity> findById(final UUID uuid) {
        return repository.findById(uuid, EntityGraphs.named(CustomerEntity.GRAPH_WITH_VEHICLES));
    }

    @Override
    protected void validateCreateConstrains(final CreateCustomerDto createDto) {
        assertEmailNotTaken(createDto.getEmail());
    }

    @Override
    protected void validateUpdateConstraints(final CustomerEntity entity, final UpdateCustomerDto updateDto) {
        assertEmailNotTaken(updateDto.getEmail());
    }

    private void assertEmailNotTaken(final String email) {
        if (StringUtils.isNotEmpty(email)) {
            if (repository.existsByEmail(email)) {
                throw new EmailTakenException(email);
            }
        }
    }
}
