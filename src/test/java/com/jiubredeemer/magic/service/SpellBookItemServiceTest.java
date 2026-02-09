package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.entity.SpellBookItem;
import com.jiubredeemer.magic.mapper.SpellBookItemDtoMapper;
import com.jiubredeemer.magic.repository.SpellBookItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellBookItemServiceTest {

    @Mock
    SpellBookItemRepository spellBookItemRepository;

    @Mock
    SpellBookItemDtoMapper spellBookItemDtoMapper;

    @InjectMocks
    SpellBookItemService spellBookItemService;

    private static SpellBookItemDto dto(UUID id) {
        SpellBookItemDto dto = new SpellBookItemDto();
        dto.setId(id);
        dto.setSpellBookId(UUID.randomUUID());
        dto.setSpellId(UUID.randomUUID());
        dto.setInUse(false);
        return dto;
    }

    private static SpellBookItem entity(UUID id) {
        SpellBookItem e = new SpellBookItem();
        e.setId(id);
        e.setSpellBookId(UUID.randomUUID());
        e.setSpellId(UUID.randomUUID());
        e.setInUse(false);
        return e;
    }

    @Test
    void create_mapsDtoToEntity_savesAndReturnsDto() {
        SpellBookItemDto request = dto(null);
        SpellBookItem entity = entity(null);
        SpellBookItem saved = entity(UUID.randomUUID());
        SpellBookItemDto responseDto = dto(saved.getId());

        when(spellBookItemDtoMapper.toEntity(request)).thenReturn(entity);
        when(spellBookItemRepository.save(entity)).thenReturn(saved);
        when(spellBookItemDtoMapper.toDto(saved)).thenReturn(responseDto);

        SpellBookItemDto result = spellBookItemService.create(request);

        assertThat(result).isEqualTo(responseDto);
        verify(spellBookItemRepository).save(entity);
    }

    @Test
    void getById_returnsDtoWhenFound() {
        UUID id = UUID.randomUUID();
        SpellBookItem entity = entity(id);
        SpellBookItemDto dto = dto(id);

        when(spellBookItemRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellBookItemDtoMapper.toDto(entity)).thenReturn(dto);

        SpellBookItemDto result = spellBookItemService.getById(id);

        assertThat(result).isEqualTo(dto);
        verify(spellBookItemRepository).findById(id);
    }

    @Test
    void list_returnsAllAsDtos() {
        SpellBookItem entity = entity(UUID.randomUUID());
        SpellBookItemDto dto = dto(entity.getId());

        when(spellBookItemRepository.findAll()).thenReturn(List.of(entity));
        when(spellBookItemDtoMapper.toDto(entity)).thenReturn(dto);

        List<SpellBookItemDto> result = spellBookItemService.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
        verify(spellBookItemRepository).findAll();
    }

    @Test
    void update_mapsDtoToEntity_savesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        SpellBookItemDto request = dto(id);
        SpellBookItem entity = entity(id);
        SpellBookItemDto responseDto = dto(id);

        when(spellBookItemRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellBookItemRepository.save(any(SpellBookItem.class))).thenReturn(entity);
        when(spellBookItemDtoMapper.toDto(entity)).thenReturn(responseDto);

        SpellBookItemDto result = spellBookItemService.update(id, request);

        assertThat(result).isEqualTo(responseDto);
        verify(spellBookItemDtoMapper).updateEntity(eq(request), eq(entity));
        verify(spellBookItemRepository).save(entity);
    }

    @Test
    void setInUse_updatesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        SpellBookItem entity = entity(id);
        entity.setInUse(false);
        SpellBookItemDto responseDto = dto(id);
        responseDto.setInUse(true);

        when(spellBookItemRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellBookItemRepository.save(entity)).thenReturn(entity);
        when(spellBookItemDtoMapper.toDto(entity)).thenReturn(responseDto);

        SpellBookItemDto result = spellBookItemService.setInUse(id, true);

        assertThat(result.getInUse()).isTrue();
        verify(spellBookItemRepository).save(argThat(e -> Boolean.TRUE.equals(e.getInUse())));
    }

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();

        spellBookItemService.delete(id);

        verify(spellBookItemRepository).deleteById(id);
    }
}
