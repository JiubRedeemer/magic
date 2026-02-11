package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.SpellRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpellService {
    private final SpellRepository spellRepository;
    private final SpellDtoMapper spellDtoMapper;

    public SpellDto create(SpellDto dto) {
        Spell entity = spellDtoMapper.toEntity(dto);
        entity.setCreatedAt(Instant.now());
        if (dto.getCharacterId() != null) {
            entity.setCreatedBy(dto.getCharacterId().toString());
        }
        Spell saved = spellRepository.save(entity);
        return spellDtoMapper.toDto(saved);
    }

    public SpellDto getById(UUID id) {
        return spellDtoMapper.toDto(spellRepository.findById(id).orElseThrow());
    }

    public List<SpellDto> list() {
        return spellDtoMapper.toDto(spellRepository.findAll());
    }

    /**
     * List spells available to the specified class (e.g. BARD, WIZARD).
     */
    public List<SpellDto> listByClass(String spellClass) {
        String code = spellClass == null ? "" : spellClass.trim().toUpperCase();
        if (code.isEmpty()) {
            return List.of();
        }
        String codePrefix = code + ", %";
        String codeSuffix = "%, " + code;
        String codeMiddle = "%, " + code + ", %";
        return spellDtoMapper.toDto(spellRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle));
    }

    public SpellDto update(UUID id, SpellDto dto) {
        Spell entity = spellRepository.findById(id).orElseThrow();
        spellDtoMapper.updateEntity(dto, entity);
        entity.setId(id);
        entity.setCreatedAt(Instant.now());
        Spell saved = spellRepository.save(entity);
        return spellDtoMapper.toDto(saved);
    }

    public void delete(UUID id) {
        spellRepository.deleteById(id);
    }
}
