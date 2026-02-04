package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.entity.SpellBookItem;
import com.jiubredeemer.magic.mapper.SpellBookItemDtoMapper;
import com.jiubredeemer.magic.repository.SpellBookItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SpellBookItemService {
    private final SpellBookItemRepository spellBookItemRepository;
    private final SpellBookItemDtoMapper spellBookItemDtoMapper;

    public SpellBookItemDto create(SpellBookItemDto dto) {
        SpellBookItem entity = spellBookItemDtoMapper.toEntity(dto);
        SpellBookItem saved = spellBookItemRepository.save(entity);
        return spellBookItemDtoMapper.toDto(saved);
    }

    public SpellBookItemDto getById(UUID id) {
        return spellBookItemDtoMapper.toDto(spellBookItemRepository.findById(id).orElseThrow());
    }

    public List<SpellBookItemDto> list() {
        return StreamSupport.stream(spellBookItemRepository.findAll().spliterator(), false)
                .map(spellBookItemDtoMapper::toDto)
                .toList();
    }

    public SpellBookItemDto update(UUID id, SpellBookItemDto dto) {
        SpellBookItem entity = spellBookItemRepository.findById(id).orElseThrow();
        spellBookItemDtoMapper.updateEntity(dto, entity);
        entity.setId(id);
        SpellBookItem saved = spellBookItemRepository.save(entity);
        return spellBookItemDtoMapper.toDto(saved);
    }

    public SpellBookItemDto setInUse(UUID id, boolean inUse) {
        SpellBookItem entity = spellBookItemRepository.findById(id).orElseThrow();
        entity.setInUse(inUse);
        SpellBookItem saved = spellBookItemRepository.save(entity);
        return spellBookItemDtoMapper.toDto(saved);
    }

    public void delete(UUID id) {
        spellBookItemRepository.deleteById(id);
    }
}
