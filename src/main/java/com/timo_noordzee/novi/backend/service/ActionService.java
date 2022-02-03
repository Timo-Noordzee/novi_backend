package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import com.timo_noordzee.novi.backend.mapper.ActionMapper;
import com.timo_noordzee.novi.backend.repository.ActionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActionService extends BaseRestService<ActionEntity, UUID, CreateActionDto, UpdateActionDto, ActionRepository, ActionMapper> {

    public ActionService(final ActionRepository repository, final ActionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    UUID parseId(final String id) {
        return parseUUID(id);
    }

    @Override
    String entityType() {
        return ActionEntity.class.getSimpleName();
    }
}
