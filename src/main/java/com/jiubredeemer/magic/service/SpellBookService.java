package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellBookDto;
import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.entity.ChargesRefillEnum;
import com.jiubredeemer.magic.entity.SpellBook;
import com.jiubredeemer.magic.entity.SpellBookItem;
import com.jiubredeemer.magic.entity.SpellCell;
import com.jiubredeemer.magic.mapper.SpellBookDtoMapper;
import com.jiubredeemer.magic.mapper.SpellBookItemDtoMapper;
import com.jiubredeemer.magic.mapper.SpellCellDtoMapper;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.SpellBookItemRepository;
import com.jiubredeemer.magic.repository.SpellBookRepository;
import com.jiubredeemer.magic.repository.SpellCellRepository;
import com.jiubredeemer.magic.repository.SpellRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SpellBookService {
    private final SpellBookRepository spellBookRepository;
    private final SpellBookDtoMapper spellBookDtoMapper;
    private final SpellBookItemRepository spellBookItemRepository;
    private final SpellBookItemDtoMapper spellBookItemDtoMapper;
    private final SpellCellRepository spellCellRepository;
    private final SpellCellDtoMapper spellCellDtoMapper;
    private final SpellRepository spellRepository;
    private final SpellDtoMapper spellDtoMapper;

    public SpellBookDto create(SpellBookDto dto) {
        SpellBook entity = spellBookDtoMapper.toEntity(dto);
        SpellBook saved = spellBookRepository.save(entity);
        return buildSpellBookDto(saved);
    }

    public SpellBookDto getById(UUID id) {
        SpellBook entity = spellBookRepository.findById(id).orElseThrow();
        return buildSpellBookDto(entity);
    }

    public List<SpellBookDto> list() {
        Iterable<SpellBook> all = spellBookRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(this::buildSpellBookDto)
                .toList();
    }

    public SpellBookDto update(UUID id, SpellBookDto dto) {
        SpellBook entity = spellBookRepository.findById(id).orElseThrow();
        spellBookDtoMapper.updateEntity(dto, entity);
        entity.setId(id);
        SpellBook saved = spellBookRepository.save(entity);
        return buildSpellBookDto(saved);
    }

    public void delete(UUID id) {
        spellBookRepository.deleteById(id);
    }

    public SpellBookDto findSpellBookByRoomIdAndCharacterId(UUID roomId, UUID characterId) {
        SpellBook spellBook = spellBookRepository.findByRoomIdAndCharacterId(roomId, characterId)
                .orElseGet(() -> {
                    SpellBook spellBookForCreate = new SpellBook();
                    spellBookForCreate.setCharacterId(characterId);
                    spellBookForCreate.setRoomId(roomId);
                    return spellBookRepository.save(spellBookForCreate);
                });
        return buildSpellBookDto(spellBook);
    }

    public SpellBookDto addSpellItemToBookByRoomIdAndCharacterId(UUID roomId, UUID characterId, SpellBookItemDto dto) {
        SpellBook spellBook = spellBookRepository.findByRoomIdAndCharacterId(roomId, characterId)
                .orElseGet(() -> {
                    SpellBook spellBookForCreate = new SpellBook();
                    spellBookForCreate.setCharacterId(characterId);
                    spellBookForCreate.setRoomId(roomId);
                    return spellBookRepository.save(spellBookForCreate);
                });
        SpellBookItem item = spellBookItemDtoMapper.toEntity(dto);
        item.setSpellBookId(spellBook.getId());
        spellBookItemRepository.save(item);
        return buildSpellBookDto(spellBook);
    }

    public SpellBookDto addSpellToBook(UUID spellBookId, UUID spellId) {
        spellBookRepository.findById(spellBookId).orElseThrow();
        spellBookItemRepository.findBySpellBookIdAndSpellId(spellBookId, spellId)
                .orElseGet(() -> {
                    SpellBookItem item = new SpellBookItem();
                    item.setSpellBookId(spellBookId);
                    item.setSpellId(spellId);
                    item.setInUse(false);
                    return spellBookItemRepository.save(item);
                });
        return getById(spellBookId);
    }

    public SpellBookDto removeSpellFromBook(UUID spellBookId, UUID spellId) {
        spellBookRepository.findById(spellBookId).orElseThrow();
        spellBookItemRepository.deleteBySpellBookIdAndSpellId(spellBookId, spellId);
        return getById(spellBookId);
    }

    public SpellBookItemDto setSpellInUse(UUID spellBookId, UUID spellId, boolean inUse) {
        SpellBookItem item = spellBookItemRepository.findBySpellBookIdAndSpellId(spellBookId, spellId)
                .orElseThrow();
        item.setInUse(inUse);
        SpellBookItem saved = spellBookItemRepository.save(item);
        return enrichSpellBookItemDto(saved);
    }

    public SpellCellDto createSpellCellForBook(UUID spellBookId, SpellCellDto dto) {
        spellBookRepository.findById(spellBookId).orElseThrow();
        SpellCell entity = spellCellDtoMapper.toEntity(dto);
        entity.setSpellBookId(spellBookId);
        SpellCell saved = spellCellRepository.save(entity);
        return spellCellDtoMapper.toDto(saved);
    }

    /**
     * Refill spell cells of the spellbook that match the given rest type (set currentCount = maxCount).
     */
    public SpellBookDto refillRest(UUID spellBookId, ChargesRefillEnum restType) {
        if (restType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "restType is required");
        }
        SpellBook spellBook = spellBookRepository.findById(spellBookId).orElseThrow();
        List<SpellCell> cells = spellCellRepository.findAllBySpellBookId(spellBookId);
        for (SpellCell cell : cells) {
            if (cell.getRefillRestType() == restType && cell.getMaxCount() != null) {
                cell.setCurrentCount(cell.getMaxCount());
                spellCellRepository.save(cell);
            }
        }
        return buildSpellBookDto(spellBook);
    }

    /**
     * Refill spell cells of the character's spellbook in the given room (by rest type).
     */
    public SpellBookDto refillRestByRoomAndCharacter(UUID roomId, UUID characterId, ChargesRefillEnum restType) {
        SpellBook spellBook = spellBookRepository.findByRoomIdAndCharacterId(roomId, characterId).orElseThrow();
        return refillRest(spellBook.getId(), restType);
    }

    private SpellBookDto buildSpellBookDto(SpellBook spellBook) {
        SpellBookDto dto = spellBookDtoMapper.toDto(spellBook);
        List<SpellBookItemDto> items = spellBookItemRepository.findAllBySpellBookId(spellBook.getId()).stream()
                .map(this::enrichSpellBookItemDto)
                .toList();
        dto.setSpells(items);
        List<SpellCell> cells = spellCellRepository.findAllBySpellBookId(spellBook.getId());
        Map<Long, SpellCellDto> cellMap = cells.stream().collect(
                Collectors.toMap(
                        SpellCell::getLevel,
                        spellCellDtoMapper::toDto,
                        (left, right) -> left,
                        LinkedHashMap::new
                )
        );
        dto.setSpellCells(cellMap);
        return dto;
    }

    private SpellBookItemDto enrichSpellBookItemDto(SpellBookItem item) {
        SpellBookItemDto dto = spellBookItemDtoMapper.toDto(item);
        if (item.getSpellId() != null) {
            spellRepository.findById(item.getSpellId())
                    .ifPresent(spell -> dto.setSpell(spellDtoMapper.toDto(spell)));
        }
        return dto;
    }

    public void deleteLogical(UUID roomId) {
        spellBookRepository.findByRoomId(roomId).forEach(spellBook -> {
            spellBook.setDeletedAt(LocalDateTime.now());
            spellBookRepository.save(spellBook);
        });
    }
}
