package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.service.SpellImageGenerationService;
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
    private final SpellImageGenerationService spellImageGenerationService;

    @PostMapping
    public SpellDto create(@RequestBody SpellDto dto) {
        return spellService.create(dto);
    }

    @GetMapping("/{id}")
    public SpellDto getById(@PathVariable UUID id) {
        return spellService.getById(id);
    }

    @GetMapping
    public List<SpellDto> list(@RequestParam(required = false) String spellClass) {
        if (spellClass != null && !spellClass.isBlank()) {
            return spellService.listByClass(spellClass);
        }
        return spellService.list();
    }

    @GetMapping("/dnd2024")
    public List<SpellDto> list2024(@RequestParam(required = false) String spellClass) {
        if (spellClass != null && !spellClass.isBlank()) {
            return spellService.list2024ByClass(spellClass);
        }
        return spellService.list2024();
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

    @PostMapping("/ai")
    public SpellDto createInAi(@RequestBody SpellDto dto) {
        return spellService.createInAi(dto);
    }

    @GetMapping("/from-spell/missing-in-ai")
    public ResponseEntity<SpellDto> getOneFromSpellMissingInAi() {
        return spellService.getOneFromSpellMissingInAi()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/from-spell/missing-image-in-ai")
    public ResponseEntity<SpellDto> getOneFromSpellMissingImageInAi() {
        return spellService.getOneFromSpellMissingInAi()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Generate and upload spell image for a row in {@code spell_ai} (by id). For n8n after the id is known.
     */
    @PostMapping("/ai/{id}/generate-image")
    public SpellDto generateImageForSpellAi(@PathVariable UUID id) {
        return spellImageGenerationService.generateAndSaveImageForSpellAi(id);
    }
}
