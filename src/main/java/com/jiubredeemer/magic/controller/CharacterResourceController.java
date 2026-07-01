package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.CharacterResourceDto;
import com.jiubredeemer.magic.service.CharacterResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/spell-books/{spellBookId}/resources")
@RequiredArgsConstructor
public class CharacterResourceController {
    private final CharacterResourceService resourceService;

    @PostMapping
    public CharacterResourceDto create(@PathVariable UUID spellBookId, @RequestBody CharacterResourceDto dto) {
        return resourceService.create(spellBookId, dto);
    }

    @PutMapping("/{id}")
    public CharacterResourceDto update(@PathVariable UUID spellBookId, @PathVariable UUID id,
                                       @RequestBody CharacterResourceDto dto) {
        return resourceService.update(id, dto);
    }

    @PostMapping("/{id}/use")
    public CharacterResourceDto use(@PathVariable UUID spellBookId, @PathVariable UUID id) {
        return resourceService.use(id);
    }

    @PostMapping("/{id}/refill")
    public CharacterResourceDto refill(@PathVariable UUID spellBookId, @PathVariable UUID id) {
        return resourceService.refill(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID spellBookId, @PathVariable UUID id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
