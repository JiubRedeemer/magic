package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.entity.SpellCell;
import com.jiubredeemer.magic.mapper.SpellCellDtoMapper;
import com.jiubredeemer.magic.repository.SpellCellRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Use one charge from the spell cell (currentCount - 1).
     *
     * @return updated spell cell
     * @throws IllegalStateException if currentCount is already 0 or null
     */
    public SpellCellDto use(UUID id) {
        SpellCell entity = spellCellRepository.findById(id).orElseThrow();
        long current = entity.getCurrentCount() != null ? entity.getCurrentCount() : 0;
        if (current <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spell cell has no charges left");
        }
        entity.setCurrentCount(current - 1);
        SpellCell saved = spellCellRepository.save(entity);
        return spellCellDtoMapper.toDto(saved);
    }
}
