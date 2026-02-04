package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.service.SpellCellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spell-cells")
@RequiredArgsConstructor
public class SpellCellController {
    private final SpellCellService spellCellService;

    @PostMapping
    public SpellCellDto create(@RequestBody SpellCellDto dto) {
        return spellCellService.create(dto);
    }

    @GetMapping("/{id}")
    public SpellCellDto getById(@PathVariable UUID id) {
        return spellCellService.getById(id);
    }

    @GetMapping
    public List<SpellCellDto> list() {
        return spellCellService.list();
    }

    @PutMapping("/{id}")
    public SpellCellDto update(@PathVariable UUID id, @RequestBody SpellCellDto dto) {
        return spellCellService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        spellCellService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
