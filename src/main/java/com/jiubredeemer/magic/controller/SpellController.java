package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeJobResponse;
import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeJobSubmission;
import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeResult;
import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.service.Spell24DescriptionSanitizeService;
import com.jiubredeemer.magic.service.SpellDescriptionSanitizeJobService;
import com.jiubredeemer.magic.service.SpellImageGenerationService;
import com.jiubredeemer.magic.service.SpellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private final Spell24DescriptionSanitizeService spell24DescriptionSanitizeService;
    private final SpellDescriptionSanitizeJobService spellDescriptionSanitizeJobService;

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

    /**
     * Strips TTG Club inline markup from {@code spell_24.description} and {@code spell_24_ai} description fields.
     * May exceed HTTP timeouts on large catalogs; prefer `POST .../sanitize-descriptions/async`.
     */
    @PostMapping("/dnd2024/sanitize-descriptions")
    public SpellDescriptionSanitizeResult sanitizeDnd2024Descriptions() {
        return spell24DescriptionSanitizeService.sanitizeAll();
    }

    /**
     * Starts sanitize in a background thread and returns immediately with a job id. Poll
     * {@code GET .../sanitize-descriptions/jobs/{jobId}} until status is DONE or FAILED.
     */
    @PostMapping("/dnd2024/sanitize-descriptions/async")
    public ResponseEntity<SpellDescriptionSanitizeJobSubmission> sanitizeDnd2024DescriptionsAsync() {
        UUID jobId = spellDescriptionSanitizeJobService.submit();
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .header(HttpHeaders.LOCATION, "/api/spells/dnd2024/sanitize-descriptions/jobs/" + jobId)
                .body(new SpellDescriptionSanitizeJobSubmission(jobId));
    }

    @GetMapping("/dnd2024/sanitize-descriptions/jobs/{jobId}")
    public ResponseEntity<SpellDescriptionSanitizeJobResponse> getSanitizeJob(@PathVariable UUID jobId) {
        return spellDescriptionSanitizeJobService.getJob(jobId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
        return spellService.getOneFromSpellMissingImageInAi()
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
