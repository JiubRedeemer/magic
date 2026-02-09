package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.SpellRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellServiceTest {

    @Mock
    SpellRepository spellRepository;

    @Mock
    SpellDtoMapper spellDtoMapper;

    @InjectMocks
    SpellService spellService;

    private static SpellDto spellDto(UUID id) {
        SpellDto dto = new SpellDto();
        dto.setId(id);
        dto.setName(Map.of("en", "Fireball"));
        dto.setLevel("3");
        dto.setSpellClass("Wizard");
        dto.setSchool("Evocation");
        return dto;
    }

    private static Spell spell(UUID id) {
        Spell entity = new Spell();
        entity.setId(id);
        entity.setName(Map.of("en", "Fireball"));
        entity.setLevel("3");
        entity.setSpellClass("Wizard");
        entity.setSchool("Evocation");
        return entity;
    }

    @Test
    void create_mapsDtoToEntity_savesAndReturnsDto() {
        SpellDto request = spellDto(null);
        Spell entity = spell(null);
        Spell savedEntity = spell(UUID.randomUUID());
        SpellDto responseDto = spellDto(savedEntity.getId());

        when(spellDtoMapper.toEntity(request)).thenReturn(entity);
        when(spellRepository.save(entity)).thenReturn(savedEntity);
        when(spellDtoMapper.toDto(savedEntity)).thenReturn(responseDto);

        SpellDto result = spellService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedEntity.getId());
        verify(spellDtoMapper).toEntity(request);
        verify(spellRepository).save(entity);
        verify(spellDtoMapper).toDto(savedEntity);
    }

    @Test
    void create_withCharacterId_setsCreatedBy() {
        UUID characterId = UUID.randomUUID();
        SpellDto request = spellDto(null);
        request.setCharacterId(characterId);
        Spell entity = spell(null);
        Spell savedEntity = spell(UUID.randomUUID());
        savedEntity.setCreatedBy(characterId.toString());
        SpellDto responseDto = spellDto(savedEntity.getId());

        when(spellDtoMapper.toEntity(request)).thenReturn(entity);
        when(spellRepository.save(any(Spell.class))).thenAnswer(inv -> {
            Spell s = inv.getArgument(0);
            if (s.getId() == null) {
                s.setId(savedEntity.getId());
            }
            s.setCreatedBy(characterId.toString());
            return s;
        });
        when(spellDtoMapper.toDto(any(Spell.class))).thenReturn(responseDto);

        spellService.create(request);

        ArgumentCaptor<Spell> captor = ArgumentCaptor.forClass(Spell.class);
        verify(spellRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedBy()).isEqualTo(characterId.toString());
    }

    @Test
    void getById_returnsDtoWhenFound() {
        UUID id = UUID.randomUUID();
        Spell entity = spell(id);
        SpellDto dto = spellDto(id);

        when(spellRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellDtoMapper.toDto(entity)).thenReturn(dto);

        SpellDto result = spellService.getById(id);

        assertThat(result).isEqualTo(dto);
        verify(spellRepository).findById(id);
        verify(spellDtoMapper).toDto(entity);
    }

    @Test
    void getById_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(spellRepository.findById(id)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> spellService.getById(id));

        verify(spellRepository).findById(id);
        verify(spellDtoMapper, never()).toDto(any(Spell.class));
    }

    @Test
    void list_returnsAllAsDtos() {
        Spell entity = spell(UUID.randomUUID());
        SpellDto dto = spellDto(entity.getId());

        List<Spell> entityList = List.of(entity);
        when(spellRepository.findAll()).thenReturn(entityList);
        when(spellDtoMapper.toDto(entityList)).thenReturn(List.of(dto));

        List<SpellDto> result = spellService.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
        verify(spellRepository).findAll();
    }

    @Test
    void listByClass_callsRepositoryWithLikeParams_returnsDtos() {
        Spell entity = spell(UUID.randomUUID());
        entity.setSpellClass("BARD, WIZARD");
        SpellDto dto = spellDto(entity.getId());
        dto.setSpellClass("BARD, WIZARD");

        List<Spell> entityList = List.of(entity);
        when(spellRepository.findBySpellClass(eq("BARD"), eq("BARD, %"), eq("%, BARD"), eq("%, BARD, %")))
                .thenReturn(entityList);
        when(spellDtoMapper.toDto(entityList)).thenReturn(List.of(dto));

        List<SpellDto> result = spellService.listByClass("BARD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpellClass()).isEqualTo("BARD, WIZARD");
        verify(spellRepository).findBySpellClass(eq("BARD"), eq("BARD, %"), eq("%, BARD"), eq("%, BARD, %"));
    }

    @Test
    void listByClass_blankOrNull_returnsEmpty() {
        assertThat(spellService.listByClass(null)).isEmpty();
        assertThat(spellService.listByClass("")).isEmpty();
        assertThat(spellService.listByClass("   ")).isEmpty();
        verify(spellRepository, never()).findBySpellClass(any(), any(), any(), any());
    }

    @Test
    void update_mapsDtoToEntity_savesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        SpellDto request = spellDto(id);
        Spell entity = spell(id);
        SpellDto responseDto = spellDto(id);

        when(spellRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellRepository.save(any(Spell.class))).thenReturn(entity);
        when(spellDtoMapper.toDto(entity)).thenReturn(responseDto);

        SpellDto result = spellService.update(id, request);

        assertThat(result).isEqualTo(responseDto);
        verify(spellDtoMapper).updateEntity(eq(request), eq(entity));
        verify(spellRepository).save(entity);
    }

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();

        spellService.delete(id);

        verify(spellRepository).deleteById(id);
    }
}
