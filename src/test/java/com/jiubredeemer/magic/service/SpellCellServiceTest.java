package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.entity.ChargesRefillEnum;
import com.jiubredeemer.magic.entity.SpellCell;
import com.jiubredeemer.magic.mapper.SpellCellDtoMapper;
import com.jiubredeemer.magic.repository.SpellCellRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SpellCellServiceTest {

    @Mock
    SpellCellRepository spellCellRepository;

    @Mock
    SpellCellDtoMapper spellCellDtoMapper;

    @InjectMocks
    SpellCellService spellCellService;

    private static SpellCellDto dto(UUID id) {
        SpellCellDto dto = new SpellCellDto();
        dto.setId(id);
        dto.setSpellBookId(UUID.randomUUID());
        dto.setLevel(1L);
        dto.setMaxCount(2L);
        dto.setCurrentCount(2L);
        dto.setRefillRestType(ChargesRefillEnum.SHORT_REST);
        return dto;
    }

    private static SpellCell entity(UUID id) {
        SpellCell e = new SpellCell();
        e.setId(id);
        e.setSpellBookId(UUID.randomUUID());
        e.setLevel(1L);
        e.setMaxCount(2L);
        e.setCurrentCount(2L);
        e.setRefillRestType(ChargesRefillEnum.SHORT_REST);
        return e;
    }

    @Test
    void create_mapsDtoToEntity_savesAndReturnsDto() {
        SpellCellDto request = dto(null);
        SpellCell entity = entity(null);
        SpellCell saved = entity(UUID.randomUUID());
        SpellCellDto responseDto = dto(saved.getId());

        when(spellCellDtoMapper.toEntity(request)).thenReturn(entity);
        when(spellCellRepository.save(entity)).thenReturn(saved);
        when(spellCellDtoMapper.toDto(saved)).thenReturn(responseDto);

        SpellCellDto result = spellCellService.create(request);

        assertThat(result).isEqualTo(responseDto);
        verify(spellCellRepository).save(entity);
    }

    @Test
    void getById_returnsDtoWhenFound() {
        UUID id = UUID.randomUUID();
        SpellCell entity = entity(id);
        SpellCellDto dto = dto(id);

        when(spellCellRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellCellDtoMapper.toDto(entity)).thenReturn(dto);

        SpellCellDto result = spellCellService.getById(id);

        assertThat(result).isEqualTo(dto);
        verify(spellCellRepository).findById(id);
    }

    @Test
    void list_returnsAllAsDtos() {
        SpellCell entity = entity(UUID.randomUUID());
        SpellCellDto dto = dto(entity.getId());

        when(spellCellRepository.findAll()).thenReturn(List.of(entity));
        when(spellCellDtoMapper.toDto(entity)).thenReturn(dto);

        List<SpellCellDto> result = spellCellService.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
        verify(spellCellRepository).findAll();
    }

    @Test
    void update_mapsDtoToEntity_savesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        SpellCellDto request = dto(id);
        SpellCell entity = entity(id);
        SpellCellDto responseDto = dto(id);

        when(spellCellRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellCellRepository.save(any(SpellCell.class))).thenReturn(entity);
        when(spellCellDtoMapper.toDto(entity)).thenReturn(responseDto);

        SpellCellDto result = spellCellService.update(id, request);

        assertThat(result).isEqualTo(responseDto);
        verify(spellCellDtoMapper).updateEntity(eq(request), eq(entity));
        verify(spellCellRepository).save(entity);
    }

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();

        spellCellService.delete(id);

        verify(spellCellRepository).deleteById(id);
    }

    @Test
    void use_decrementsCurrentCountAndSaves() {
        UUID id = UUID.randomUUID();
        SpellCell entity = entity(id);
        entity.setCurrentCount(2L);
        SpellCell saved = entity(id);
        saved.setCurrentCount(1L);
        SpellCellDto responseDto = dto(id);
        responseDto.setCurrentCount(1L);

        when(spellCellRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellCellRepository.save(any(SpellCell.class))).thenReturn(saved);
        when(spellCellDtoMapper.toDto(saved)).thenReturn(responseDto);

        SpellCellDto result = spellCellService.use(id);

        assertThat(result.getCurrentCount()).isEqualTo(1L);
        assertThat(entity.getCurrentCount()).isEqualTo(1L);
        verify(spellCellRepository).save(entity);
    }

    @Test
    void use_throwsWhenNoChargesLeft() {
        UUID id = UUID.randomUUID();
        SpellCell entity = entity(id);
        entity.setCurrentCount(0L);

        when(spellCellRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(ResponseStatusException.class, () -> spellCellService.use(id));
        verify(spellCellRepository, never()).save(any());
    }
}
