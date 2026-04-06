package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.entity.Spell24Ai;
import com.jiubredeemer.magic.entity.SpellAi;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.Spell24AiRepository;
import com.jiubredeemer.magic.repository.SpellAiRepository;
import com.jiubredeemer.magic.repository.SpellRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpellService {
    private final SpellStorageService spellStorageService;
    private final SpellRepository spellRepository;
    private final Spell24AiRepository spell24AiRepository;
    private final EntityManager entityManager;
    private final SpellDtoMapper spellDtoMapper;

    public SpellDto create(SpellDto dto) {
        Spell entity = spellDtoMapper.toEntity(dto);
        entity.setCreatedAt(Instant.now());
        if (dto.getCharacterId() != null) {
            entity.setCreatedBy(dto.getCharacterId().toString());
        }
        Spell saved = spellStorageService.save(entity);
        return spellDtoMapper.toDto(saved);
    }

    public SpellDto getById(UUID id) {
        return spellDtoMapper.toDto(spellStorageService.findById(id).orElseThrow());
    }

    public List<SpellDto> list() {
        return spellDtoMapper.toDto(spellStorageService.findAll());
    }

    /** Lists only DnD 2024 spells. */
    public List<SpellDto> list2024() {
        return spellDtoMapper.toDto(spellStorageService.findAll24());
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
        return spellDtoMapper.toDto(spellStorageService.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle));
    }

    /** Lists DnD 2024 spells filtered by class. */
    public List<SpellDto> list2024ByClass(String spellClass) {
        String code = spellClass == null ? "" : spellClass.trim().toUpperCase();
        if (code.isEmpty()) {
            return List.of();
        }
        String codePrefix = code + ", %";
        String codeSuffix = "%, " + code;
        String codeMiddle = "%, " + code + ", %";
        return spellDtoMapper.toDto(spellStorageService.findBySpellClass24(code, codePrefix, codeSuffix, codeMiddle));
    }

    public SpellDto update(UUID id, SpellDto dto) {
        Spell entity = spellStorageService.findById(id).orElseThrow();
        spellDtoMapper.updateEntity(dto, entity);
        entity.setId(id);
        entity.setCreatedAt(Instant.now());
        Spell saved = spellStorageService.save(entity);
        return spellDtoMapper.toDto(saved);
    }

    public void delete(UUID id) {
        spellStorageService.deleteById(id);
    }

    /**
     * Inserts or updates {@code spell_ai}. When {@code id} is set, Spring Data {@code save} would call
     * {@code merge} and fail for new rows — we use {@code persist} for insert with a client-provided id.
     */
    @Transactional
    public SpellDto createInAi(SpellDto dto) {
        Spell24Ai entity = new Spell24Ai();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreatedAt(Instant.now());

        UUID id = dto.getId();
        Spell24Ai saved;
        if (id != null) {
            Optional<Spell24Ai> existingOpt = spell24AiRepository.findById(id);
            if (existingOpt.isPresent()) {
                Spell24Ai existing = existingOpt.get();
                BeanUtils.copyProperties(dto, existing);
                if (existing.getCreatedAt() == null) {
                    existing.setCreatedAt(Instant.now());
                }
                saved = spell24AiRepository.save(existing);
            } else {
                entityManager.persist(entity);
                entityManager.flush();
                saved = entity;
            }
        } else {
            saved = spell24AiRepository.save(entity);
        }

        Spell spell = new Spell();
        BeanUtils.copyProperties(saved, spell);
        return spellDtoMapper.toDto(spell);
    }

    public Optional<SpellDto> getOneFromSpellMissingInAi() {
        return spell24AiRepository.findOneMissingInAi().map(spellDtoMapper::toDto);
    }

    public  Optional<SpellDto> getOneFromSpellMissingImageInAi() {
        return spell24AiRepository.findOneFromSpellMissingImageInAi().map(spellDtoMapper::toDto);
    }
}
