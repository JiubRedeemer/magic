package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.entity.SpellCell;
import com.jiubredeemer.magic.mapper.SpellCellDtoMapper;
import com.jiubredeemer.magic.repository.SpellCellRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SpellCellService {
    private final SpellCellRepository spellCellRepository;
    private final SpellCellDtoMapper spellCellDtoMapper;

    public SpellCellDto create(SpellCellDto dto) {
        SpellCell entity = spellCellDtoMapper.toEntity(dto);
        SpellCell saved = spellCellRepository.save(entity);
        return spellCellDtoMapper.toDto(saved);
    }

    public SpellCellDto getById(UUID id) {
        return spellCellDtoMapper.toDto(spellCellRepository.findById(id).orElseThrow());
    }

    public List<SpellCellDto> list() {
        return StreamSupport.stream(spellCellRepository.findAll().spliterator(), false)
                .map(spellCellDtoMapper::toDto)
                .toList();
    }

    public SpellCellDto update(UUID id, SpellCellDto dto) {
        SpellCell entity = spellCellRepository.findById(id).orElseThrow();
        spellCellDtoMapper.updateEntity(dto, entity);
        entity.setId(id);
        SpellCell saved = spellCellRepository.save(entity);
        return spellCellDtoMapper.toDto(saved);
    }

    public void delete(UUID id) {
        spellCellRepository.deleteById(id);
    }
}
