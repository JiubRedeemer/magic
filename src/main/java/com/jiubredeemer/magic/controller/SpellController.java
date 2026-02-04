package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.service.SpellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spells")
@RequiredArgsConstructor
public class SpellController {
    private final SpellService spellService;

    @PostMapping
    public SpellDto create(@RequestBody SpellDto dto) {
        return spellService.create(dto);
    }

    @GetMapping("/{id}")
    public SpellDto getById(@PathVariable UUID id) {
        return spellService.getById(id);
    }

    @GetMapping
    public List<SpellDto> list() {
        return spellService.list();
    }

    @PutMapping("/{id}")
    public SpellDto update(@PathVariable UUID id, @RequestBody SpellDto dto) {
        return spellService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        spellService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
