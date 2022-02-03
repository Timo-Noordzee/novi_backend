package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.data.ActionEntity;
import com.timo_noordzee.novi.backend.dto.CreateActionDto;
import com.timo_noordzee.novi.backend.dto.UpdateActionDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.mapper.ActionMapper;
import com.timo_noordzee.novi.backend.repository.ActionRepository;
import com.timo_noordzee.novi.backend.util.ActionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActionServiceTest {

    private final ActionTestUtils actionTestUtils = new ActionTestUtils();

    @Mock
    private ActionRepository actionRepository;

    private ActionService actionService;

    @BeforeEach
    void setUp() {
        final ActionMapper actionMapper = Mappers.getMapper(ActionMapper.class);
        actionService = new ActionService(actionRepository, actionMapper);
    }

    @Test
    void parsingInvalidIdThrowsInvalidUUIDException() {
        final String invalidUUID = "invalid-id";

        assertThrows(InvalidUUIDException.class, () -> actionService.parseId(invalidUUID));
    }

    @Test
    void getByIdForNonexistentActionThrowsEntityNotFoundException() {
        when(actionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> actionService.getById(id));
    }

    @Test
    void addValidActionReturnsActionEntity() {
        final CreateActionDto createActionDto = actionTestUtils.generateMockCreateDto();
        when(actionRepository.save(any(ActionEntity.class))).thenAnswer(i -> i.getArgument(0));

        final ActionEntity actionEntity = actionService.add(createActionDto);

        assertThat(actionEntity).isNotNull();
        assertThat(actionEntity.getId()).isNotNull();
        assertThat(actionEntity.getName()).isEqualTo(createActionDto.getName());
        assertThat(actionEntity.getPrice()).isEqualTo(createActionDto.getPrice());
    }

    @Test
    void updateNonexistentActionThrowsEntityNotFoundException() {
        when(actionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();
        final UpdateActionDto updateActionDto = actionTestUtils.generateMockUpdateDto();

        assertThrows(EntityNotFoundException.class, () -> actionService.update(id, updateActionDto));
    }

    @Test
    void updateActionEntityWorks() {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        when(actionRepository.findById(any(UUID.class))).thenReturn(Optional.of(actionEntity));
        final String id = UUID.randomUUID().toString();
        final UpdateActionDto updateActionDto = actionTestUtils.generateMockUpdateDto();
        when(actionRepository.save(any(ActionEntity.class))).thenAnswer(i -> i.getArgument(0));

        final ActionEntity updatedActionEntity = actionService.update(id, updateActionDto);

        assertThat(updatedActionEntity).isNotNull();
        assertThat(updatedActionEntity.getId()).isEqualTo(actionEntity.getId());
        assertThat(updatedActionEntity.getName()).isEqualTo(updateActionDto.getName());
        assertThat(updatedActionEntity.getPrice()).isEqualTo(updateActionDto.getPrice());
    }

    @Test
    void deleteNonexistentActionEntityThrowsEntityNotFoundException() {
        when(actionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> actionService.deleteById(id));
    }

    @Test
    void deleteActionWorks() {
        final ActionEntity actionEntity = actionTestUtils.generateMockEntity();
        when(actionRepository.findById(any(UUID.class))).thenReturn(Optional.of(actionEntity));
        final String id = UUID.randomUUID().toString();

        final ActionEntity deletedActionEntity = actionService.deleteById(id);

        assertThat(deletedActionEntity).isNotNull();
        assertThat(deletedActionEntity.getId()).isEqualTo(actionEntity.getId());
        assertThat(deletedActionEntity.getName()).isEqualTo(actionEntity.getName());
        assertThat(deletedActionEntity.getPrice()).isEqualTo(actionEntity.getPrice());
    }

}