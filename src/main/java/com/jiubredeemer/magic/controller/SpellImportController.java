package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.service.TtgSpellImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spells")
public class SpellImportController {

    private final TtgSpellImportService importService;

    public SpellImportController(TtgSpellImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import")
    public ResponseEntity<TtgSpellImportService.ImportResult> importSpells() {
        TtgSpellImportService.ImportResult result = importService.importSpells();
        return ResponseEntity.ok(result);
    }
}
