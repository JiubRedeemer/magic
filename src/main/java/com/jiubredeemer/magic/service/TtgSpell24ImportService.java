package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.client.TtgClubV2ApiClient;
import com.jiubredeemer.magic.dto.spellbook.SpellImportResult;
import com.jiubredeemer.magic.dto.ttg.v2.TtgV2SpellDetail;
import com.jiubredeemer.magic.dto.ttg.v2.TtgV2SpellListItem;
import com.jiubredeemer.magic.entity.Spell24;
import com.jiubredeemer.magic.service.ttg.TtgSpellFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Imports DnD 2024 spells from new.ttg.club API v2 into {@code magic.spell_24} / {@code magic.spell_24_ai}.
 */
@Service
public class TtgSpell24ImportService {

    private static final Logger log = LoggerFactory.getLogger(TtgSpell24ImportService.class);
    private static final int PAGE_SIZE = 100;
    private static final int PROGRESS_LOG_INTERVAL = 50;

    private final TtgClubV2ApiClient ttgClubV2ApiClient;
    private final SpellStorageService spellStorageService;

    @Value("${ttg2024.api.detail-request-delay-ms:0}")
    private long detailRequestDelayMs;

    public TtgSpell24ImportService(@Qualifier("ttg2024") TtgClubV2ApiClient ttgClubV2ApiClient,
                                   SpellStorageService spellStorageService) {
        this.ttgClubV2ApiClient = ttgClubV2ApiClient;
        this.spellStorageService = spellStorageService;
    }

    public SpellImportResult importSpells() {
        log.info("Starting TTG spell import (DnD 2024, API v2)");

        List<TtgV2SpellListItem> allItems = fetchAllSpellListItems();
        log.info("Fetched {} spell list items from TTG 2024", allItems.size());

        int imported = 0;
        int updated = 0;
        int failed = 0;
        int total = allItems.size();

        for (int i = 0; i < total; i++) {
            TtgV2SpellListItem item = allItems.get(i);
            String slug = item.slug();
            if (slug == null || slug.isBlank()) {
                log.warn("Skipping spell with blank url/slug");
                failed++;
                logProgress(i + 1, total, imported, updated, failed, "invalid");
                continue;
            }

            try {
                Spell24 spell = spellStorageService.findSpell24ForImportBySlug(slug)
                        .orElse(new Spell24());

                // search endpoint already contains concentration/ritual and basic metadata
                TtgSpellFormatting.mapV2ListItemToSpell24(item, spell);

                sleepBeforeTtgDetailRequest();
                TtgV2SpellDetail detail = ttgClubV2ApiClient.fetchSpellDetail(slug);
                TtgSpellFormatting.mapV2DetailToSpell24(detail, spell);

                boolean isNew = spell.getId() == null;
                if (isNew) {
                    spell.setCreatedBy("TTG");
                }
                spellStorageService.saveTtgSpell24(spell);

                if (isNew) {
                    imported++;
                } else {
                    updated++;
                }
            } catch (Exception e) {
                log.warn("Failed to import spell {}: {}", slug, e.getMessage());
                failed++;
            }
            logProgress(i + 1, total, imported, updated, failed, slug);
        }

        SpellImportResult result = new SpellImportResult(allItems.size(), imported, updated, failed);
        log.info("TTG 2024 spell import completed: {}", result);
        return result;
    }

    private List<TtgV2SpellListItem> fetchAllSpellListItems() {
        List<TtgV2SpellListItem> all = new java.util.ArrayList<>();
        int page = 0;

        while (true) {
            List<TtgV2SpellListItem> pageItems = ttgClubV2ApiClient.fetchSpellList(page, PAGE_SIZE);
            if (pageItems.isEmpty()) {
                break;
            }
            all.addAll(pageItems);
            if (pageItems.size() < PAGE_SIZE) {
                break;
            }
            page++;
        }
        return all;
    }

    private void sleepBeforeTtgDetailRequest() {
        if (detailRequestDelayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(detailRequestDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting before TTG request", e);
        }
    }

    private void logProgress(int processed, int total, int imported, int updated, int failed, String currentSlug) {
        if (processed % PROGRESS_LOG_INTERVAL == 0 || processed == total) {
            log.info("Import 2024 progress: {}/{} (imported: {}, updated: {}, failed: {}) - current: {}",
                    processed, total, imported, updated, failed, currentSlug);
        }
    }
}
