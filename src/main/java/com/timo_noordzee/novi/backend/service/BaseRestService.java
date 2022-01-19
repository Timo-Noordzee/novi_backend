package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.domain.CreateDto;
import com.timo_noordzee.novi.backend.exception.EntityAlreadyExistsException;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.mapper.EntityMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public abstract class BaseRestService<E, ID, C extends CreateDto, U, R extends JpaRepository<E, ID>, M extends EntityMapper<E, C, U>> {

    protected final R repository;
    protected final M mapper;

    public BaseRestService(final R repository, final M mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    abstract ID parseId(final String id);

    abstract String entityType();

    public List<E> getAll() {
        return repository.findAll();
    }

    public E getById(final String id) {
        final ID actualId = parseId(id);
        return repository.findById(actualId).orElseThrow(() -> new EntityNotFoundException(id, entityType()));
    }

    public E deleteById(final String id) {
        final E entity = getById(id);
        repository.delete(entity);
        return entity;
    }

    public E add(final C createDto) {
        final String id = createDto.getId();
        if (StringUtils.isNotEmpty(id)) {
            final ID actualId = parseId(id);
            final boolean exists = repository.existsById(actualId);
            if (exists) {
                throw new EntityAlreadyExistsException(id, entityType());
            }
        }

        validateCreateConstrains(createDto);

        final E entity = fromCreateDto(createDto);
        return repository.save(entity);
    }

    public E update(final String id, final U updateDto) {
        final E entity = getById(id);
        validateUpdateConstraints(entity, updateDto);
        updateWithDto(entity, updateDto);
        return repository.save(entity);
    }

    protected void validateCreateConstrains(final C createDto) {
    }

    protected void validateUpdateConstraints(final E entity, final U updateDto) {
    }

    protected E fromCreateDto(final C createDto) {
        return mapper.fromCreateDto(createDto);
    }

    protected E updateWithDto(final E entity, final U updateDto) {
        return mapper.updateWithDto(updateDto, entity);
    }

    protected UUID parseUUID(final String id) {
        try {
            return UUID.fromString(id);
        } catch (final Exception ignore) {
            throw new InvalidUUIDException(id);
        }
    }

}
