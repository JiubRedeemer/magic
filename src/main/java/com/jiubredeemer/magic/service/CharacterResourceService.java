package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.CharacterResourceDto;
import com.jiubredeemer.magic.entity.CharacterResource;
import com.jiubredeemer.magic.entity.ChargesRefillEnum;
import com.jiubredeemer.magic.mapper.CharacterResourceDtoMapper;
import com.jiubredeemer.magic.repository.CharacterResourceRepository;
import com.jiubredeemer.magic.repository.SpellBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CharacterResourceService {
    private final CharacterResourceRepository resourceRepository;
    private final CharacterResourceDtoMapper mapper;
    private final SpellBookRepository spellBookRepository;

    public CharacterResourceDto create(UUID spellBookId, CharacterResourceDto dto) {
        spellBookRepository.findById(spellBookId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SpellBook not found"));
        CharacterResource entity = mapper.toEntity(dto);
        entity.setSpellBookId(spellBookId);
        if (entity.getCurrentCount() == null) entity.setCurrentCount(entity.getMaxCount());
        return mapper.toDto(resourceRepository.save(entity));
    }

    public CharacterResourceDto update(UUID id, CharacterResourceDto dto) {
        CharacterResource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapper.updateEntity(dto, entity);
        entity.setId(id);
        return mapper.toDto(resourceRepository.save(entity));
    }

    public CharacterResourceDto use(UUID id) {
        CharacterResource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        long current = entity.getCurrentCount() != null ? entity.getCurrentCount() : 0;
        if (current <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No charges left");
        entity.setCurrentCount(current - 1);
        return mapper.toDto(resourceRepository.save(entity));
    }

    public CharacterResourceDto refill(UUID id) {
        CharacterResource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        long current = entity.getCurrentCount() != null ? entity.getCurrentCount() : 0;
        long max = entity.getMaxCount() != null ? entity.getMaxCount() : 0;
        if (current >= max) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already full");
        entity.setCurrentCount(current + 1);
        return mapper.toDto(resourceRepository.save(entity));
    }

    public void delete(UUID id) {
        resourceRepository.deleteById(id);
    }

    public List<CharacterResourceDto> findBySpellBook(UUID spellBookId) {
        return mapper.toDto(resourceRepository.findAllBySpellBookId(spellBookId));
    }

    /** Восстанавливает заряды ресурсов книги согласно типу отдыха. */
    public void refillByRestType(UUID spellBookId, ChargesRefillEnum restType) {
        List<CharacterResource> resources = resourceRepository.findAllBySpellBookId(spellBookId);
        for (CharacterResource r : resources) {
            boolean shouldRefill = r.getRefillRestType() == restType
                    || (restType == ChargesRefillEnum.LONG_REST && r.getRefillRestType() == ChargesRefillEnum.SHORT_REST);
            if (shouldRefill && r.getMaxCount() != null) {
                r.setCurrentCount(r.getMaxCount());
                resourceRepository.save(r);
            }
        }
    }
}
