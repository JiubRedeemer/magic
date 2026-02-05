package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.RefillRestRequest;
import com.jiubredeemer.magic.dto.spellbook.SpellBookDto;
import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.service.SpellBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spell-books")
@RequiredArgsConstructor
public class SpellBookController {
    private final SpellBookService spellBookService;

    @PostMapping
    public SpellBookDto create(@RequestBody SpellBookDto dto) {
        return spellBookService.create(dto);
    }

    @GetMapping("/{id}")
    public SpellBookDto getById(@PathVariable UUID id) {
        return spellBookService.getById(id);
    }

    @GetMapping
    public List<SpellBookDto> list() {
        return spellBookService.list();
    }

    @PutMapping("/{id}")
    public SpellBookDto update(@PathVariable UUID id, @RequestBody SpellBookDto dto) {
        return spellBookService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        spellBookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-room-character")
    public SpellBookDto getByRoomAndCharacter(@RequestParam UUID roomId, @RequestParam UUID characterId) {
        return spellBookService.findSpellBookByRoomIdAndCharacterId(roomId, characterId);
    }

    @PostMapping("/{spellBookId}/spells/{spellId}")
    public SpellBookDto addSpell(@PathVariable UUID spellBookId, @PathVariable UUID spellId) {
        return spellBookService.addSpellToBook(spellBookId, spellId);
    }

    @DeleteMapping("/{spellBookId}/spells/{spellId}")
    public SpellBookDto removeSpell(@PathVariable UUID spellBookId, @PathVariable UUID spellId) {
        return spellBookService.removeSpellFromBook(spellBookId, spellId);
    }

    @PatchMapping("/{spellBookId}/spells/{spellId}/in-use")
    public SpellBookItemDto setSpellInUse(
            @PathVariable UUID spellBookId,
            @PathVariable UUID spellId,
            @RequestParam boolean inUse
    ) {
        return spellBookService.setSpellInUse(spellBookId, spellId, inUse);
    }

    @PostMapping("/{spellBookId}/spell-cells")
    public SpellCellDto createSpellCell(
            @PathVariable UUID spellBookId,
            @RequestBody SpellCellDto dto
    ) {
        return spellBookService.createSpellCellForBook(spellBookId, dto);
    }

    @PostMapping("/{spellBookId}/rest")
    public SpellBookDto refillRest(
            @PathVariable UUID spellBookId,
            @RequestBody RefillRestRequest request
    ) {
        return spellBookService.refillRest(spellBookId, request.getRestType());
    }

    @PostMapping("/by-room-character/rest")
    public SpellBookDto refillRestByCharacter(
            @RequestParam UUID roomId,
            @RequestParam UUID characterId,
            @RequestParam com.jiubredeemer.magic.entity.ChargesRefillEnum restType
    ) {
        return spellBookService.refillRestByRoomAndCharacter(roomId, characterId, restType);
    }
}
