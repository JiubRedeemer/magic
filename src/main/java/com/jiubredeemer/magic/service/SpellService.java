package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.SpellRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpellService {
    private final SpellRepository spellRepository;
    private final SpellDtoMapper spellDtoMapper;

    public SpellDto create(SpellDto dto) {
        Spell entity = spellDtoMapper.toEntity(dto);
        Spell saved = spellRepository.save(entity);
        return spellDtoMapper.toDto(saved);
    }

    public SpellDto getById(UUID id) {
        return spellDtoMapper.toDto(spellRepository.findById(id).orElseThrow());
    }

    public List<SpellDto> list() {
        return spellDtoMapper.toDto(spellRepository.findAll());
    }

    public SpellDto update(UUID id, SpellDto dto) {
        Spell entity = spellRepository.findById(id).orElseThrow();
        spellDtoMapper.updateEntity(dto, entity);
        entity.setId(id);
        Spell saved = spellRepository.save(entity);
        return spellDtoMapper.toDto(saved);
    }

    public void delete(UUID id) {
        spellRepository.deleteById(id);
    }
}
