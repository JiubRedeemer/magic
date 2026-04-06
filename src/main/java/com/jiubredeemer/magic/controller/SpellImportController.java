package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellImportResult;
import com.jiubredeemer.magic.service.TtgSpell24ImportService;
import com.jiubredeemer.magic.service.TtgSpellImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spells")
public class SpellImportController {

    private final TtgSpellImportService importService;
    private final TtgSpell24ImportService import24Service;

    public SpellImportController(TtgSpellImportService importService,
                                 TtgSpell24ImportService import24Service) {
        this.importService = importService;
        this.import24Service = import24Service;
    }

    @PostMapping("/import")
    public ResponseEntity<SpellImportResult> importSpells() {
        SpellImportResult result = importService.importSpells();
        return ResponseEntity.ok(result);
    }

    /** DnD 2024 spells from new.ttg.club into {@code spell_24} / {@code spell_24_ai}. */
    @PostMapping("/import-2024")
    public ResponseEntity<SpellImportResult> importSpells2024() {
        SpellImportResult result = import24Service.importSpells();
        return ResponseEntity.ok(result);
    }
}
