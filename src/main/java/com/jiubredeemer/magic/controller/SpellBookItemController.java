package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.service.SpellBookItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spell-book-items")
@RequiredArgsConstructor
public class SpellBookItemController {
    private final SpellBookItemService spellBookItemService;

    @PostMapping
    public SpellBookItemDto create(@RequestBody SpellBookItemDto dto) {
        return spellBookItemService.create(dto);
    }

    @GetMapping("/{id}")
    public SpellBookItemDto getById(@PathVariable UUID id) {
        return spellBookItemService.getById(id);
    }

    @GetMapping
    public List<SpellBookItemDto> list() {
        return spellBookItemService.list();
    }

    @PutMapping("/{id}")
    public SpellBookItemDto update(@PathVariable UUID id, @RequestBody SpellBookItemDto dto) {
        return spellBookItemService.update(id, dto);
    }

    @PatchMapping("/{id}/in-use")
    public SpellBookItemDto setInUse(@PathVariable UUID id, @RequestParam boolean inUse) {
        return spellBookItemService.setInUse(id, inUse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        spellBookItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
