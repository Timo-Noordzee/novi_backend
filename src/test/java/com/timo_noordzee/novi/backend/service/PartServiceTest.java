package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.dto.CreatePartDto;
import com.timo_noordzee.novi.backend.dto.UpdatePartDto;
import com.timo_noordzee.novi.backend.exception.EntityNotFoundException;
import com.timo_noordzee.novi.backend.exception.InvalidUUIDException;
import com.timo_noordzee.novi.backend.mapper.PartMapper;
import com.timo_noordzee.novi.backend.repository.PartRepository;
import com.timo_noordzee.novi.backend.util.PartTestUtils;
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
public class PartServiceTest {

    private final PartTestUtils partTestUtils = new PartTestUtils();

    @Mock
    private PartRepository partRepository;

    private PartService partService;

    @BeforeEach
    void setUp() {
        final PartMapper partMapper = Mappers.getMapper(PartMapper.class);
        partService = new PartService(partRepository, partMapper);
    }

    @Test
    void parsingInvalidIdThrowsInvalidUUIDException() {
        final String invalidUUID = "invalid-id";

        assertThrows(InvalidUUIDException.class, () -> partService.parseId(invalidUUID));
    }

    @Test
    void getByIdForNonexistentPartThrowsEntityNotFoundException() {
        when(partRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> partService.getById(id));
    }

    @Test
    void addValidPartReturnsPartEntity() {
        final CreatePartDto createPartDto = partTestUtils.generateMockCreateDto();
        when(partRepository.save(any(PartEntity.class))).thenAnswer(i -> i.getArgument(0));

        final PartEntity partEntity = partService.add(createPartDto);

        assertThat(partEntity).isNotNull();
        assertThat(partEntity.getId()).isNotNull();
        assertThat(partEntity.getName()).isEqualTo(createPartDto.getName());
        assertThat(partEntity.getPrice()).isEqualTo(createPartDto.getPrice());
        assertThat(partEntity.getStock()).isEqualTo(createPartDto.getStock());
    }

    @Test
    void updateNonexistentPartThrowsEntityNotFoundException() {
        when(partRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();
        final UpdatePartDto updatePartDto = partTestUtils.generateMockUpdateDto();

        assertThrows(EntityNotFoundException.class, () -> partService.update(id, updatePartDto));
    }

    @Test
    void updatePartEntityWorks() {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        when(partRepository.findById(any(UUID.class))).thenReturn(Optional.of(partEntity));
        final String id = UUID.randomUUID().toString();
        final UpdatePartDto updatePartDto = partTestUtils.generateMockUpdateDto();
        when(partRepository.save(any(PartEntity.class))).thenAnswer(i -> i.getArgument(0));

        final PartEntity updatedPartEntity = partService.update(id, updatePartDto);

        assertThat(updatedPartEntity).isNotNull();
        assertThat(updatedPartEntity.getId()).isEqualTo(partEntity.getId());
        assertThat(updatedPartEntity.getName()).isEqualTo(updatePartDto.getName());
        assertThat(updatedPartEntity.getStock()).isEqualTo(updatePartDto.getStock());
        assertThat(updatedPartEntity.getPrice()).isEqualTo(updatePartDto.getPrice());
    }

    @Test
    void deleteNonexistentPartEntityThrowsEntityNotFoundException() {
        when(partRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        final String id = UUID.randomUUID().toString();

        assertThrows(EntityNotFoundException.class, () -> partService.deleteById(id));
    }

    @Test
    void deletePartWorks() {
        final PartEntity partEntity = partTestUtils.generateMockEntity();
        when(partRepository.findById(any(UUID.class))).thenReturn(Optional.of(partEntity));
        final String id = UUID.randomUUID().toString();

        final PartEntity deletedPartEntity = partService.deleteById(id);

        assertThat(deletedPartEntity).isNotNull();
        assertThat(deletedPartEntity.getId()).isEqualTo(partEntity.getId());
        assertThat(deletedPartEntity.getName()).isEqualTo(partEntity.getName());
        assertThat(deletedPartEntity.getPrice()).isEqualTo(partEntity.getPrice());
        assertThat(deletedPartEntity.getStock()).isEqualTo(partEntity.getStock());
    }

}