package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.client.TtgApiClient;
import com.jiubredeemer.magic.dto.ttg.TtgComponents;
import com.jiubredeemer.magic.dto.ttg.TtgSpellDetail;
import com.jiubredeemer.magic.dto.ttg.TtgSpellListItem;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.repository.SpellRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TtgSpellImportService {

    private static final Logger log = LoggerFactory.getLogger(TtgSpellImportService.class);
    private static final int PAGE_SIZE = 1000;
    private static final int PROGRESS_LOG_INTERVAL = 50;

    private final TtgApiClient ttgApiClient;
    private final SpellRepository spellRepository;

    public TtgSpellImportService(TtgApiClient ttgApiClient, SpellRepository spellRepository) {
        this.ttgApiClient = ttgApiClient;
        this.spellRepository = spellRepository;
    }

    public ImportResult importSpells() {
        log.info("Starting TTG spell import");

        List<TtgSpellListItem> allItems = fetchAllSpellListItems();
        log.info("Fetched {} spell list items from TTG", allItems.size());

        int imported = 0;
        int updated = 0;
        int failed = 0;
        int total = allItems.size();

        for (int i = 0; i < total; i++) {
            TtgSpellListItem item = allItems.get(i);
            String slug = item.getSlug();
            if (slug == null || slug.isBlank()) {
                log.warn("Skipping spell with invalid URL: {}", item.url());
                failed++;
                logProgress(i + 1, total, imported, updated, failed, "invalid");
                continue;
            }

            try {
                Spell spell = spellRepository.findByTtgSlug(slug)
                        .orElse(new Spell());

                TtgSpellDetail detail = ttgApiClient.fetchSpellDetail(slug);
                mapDetailToSpell(detail, spell);

                boolean isNew = spell.getId() == null;
                if (isNew) {
                    spell.setCreatedBy("TTG");
                }
                spellRepository.save(spell);

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

        ImportResult result = new ImportResult(allItems.size(), imported, updated, failed);
        log.info("TTG spell import completed: {}", result);
        return result;
    }

    private List<TtgSpellListItem> fetchAllSpellListItems() {
        List<TtgSpellListItem> all = new java.util.ArrayList<>();
        int page = 0;

        while (true) {
            List<TtgSpellListItem> pageItems = ttgApiClient.fetchSpellList(page, PAGE_SIZE);
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

    private void mapDetailToSpell(TtgSpellDetail detail, Spell spell) {
        spell.setName(detail.name() != null ? detail.name() : Map.of());
        spell.setLevel(detail.level() != null ? String.valueOf(detail.level()) : "0");
        spell.setSchool(detail.school() != null ? detail.school() : "");
        spell.setSpellClass(formatClasses(detail.classes()));
        spell.setRitual(Boolean.TRUE.equals(detail.ritual()));
        spell.setCustomization(Boolean.TRUE.equals(detail.concentration()));
        spell.setUseTime(detail.time());
        spell.setDistance(detail.range());
        spell.setDuration(detail.duration());
        spell.setComponents(formatComponents(detail.components()));
        spell.setDescription(detail.description());
        spell.setTtgSlug(extractSlug(detail.url()));
    }

    /** Maps display names (e.g. Russian) from TTG to coded spell class designations. */
    private static final Map<String, String> SPELL_CLASS_NAME_TO_CODE = Map.ofEntries(
            Map.entry("Бард", "BARD"),
            Map.entry("Волшебник", "WIZARD"),
            Map.entry("Друид", "DRUID"),
            Map.entry("Жрец", "CLERIC"),
            Map.entry("Изобретатель", "ARTIFICER"),
            Map.entry("Колдун", "WARLOCK"),
            Map.entry("Магус", "WIZARD"),
            Map.entry("Паладин", "PALADIN"),
            Map.entry("Следопыт", "RANGER"),
            Map.entry("Чародей", "SORCERER"),
            Map.entry("Шаман", "DRUID"),
            Map.entry("BARD", "BARD"),
            Map.entry("BARBARIAN", "BARBARIAN"),
            Map.entry("FIGHTER", "FIGHTER"),
            Map.entry("WIZARD", "WIZARD"),
            Map.entry("DRUID", "DRUID"),
            Map.entry("CLERIC", "CLERIC"),
            Map.entry("ARTIFICER", "ARTIFICER"),
            Map.entry("WARLOCK", "WARLOCK"),
            Map.entry("MONK", "MONK"),
            Map.entry("PALADIN", "PALADIN"),
            Map.entry("ROGUE", "ROGUE"),
            Map.entry("RANGER", "RANGER"),
            Map.entry("SORCERER", "SORCERER")
    );

    private String formatClasses(List<com.jiubredeemer.magic.dto.ttg.TtgSpellClass> classes) {
        if (classes == null || classes.isEmpty()) {
            return "";
        }
        return classes.stream()
                .map(com.jiubredeemer.magic.dto.ttg.TtgSpellClass::name)
                .map(name -> name != null ? SPELL_CLASS_NAME_TO_CODE.getOrDefault(name.trim(), name) : name)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private String formatComponents(TtgComponents components) {
        if (components == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(components.v())) sb.append("V");
        if (Boolean.TRUE.equals(components.s())) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append("S");
        }
        if (hasMaterialComponent(components.m())) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append("M");
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    private boolean hasMaterialComponent(Object m) {
        if (m == null) return false;
        if (m instanceof Boolean b) return b;
        if (m instanceof String s) return !s.isBlank();
        return false;
    }

    private void logProgress(int processed, int total, int imported, int updated, int failed, String currentSlug) {
        if (processed % PROGRESS_LOG_INTERVAL == 0 || processed == total) {
            log.info("Import progress: {}/{} (imported: {}, updated: {}, failed: {}) - current: {}",
                    processed, total, imported, updated, failed, currentSlug);
        }
    }

    private String extractSlug(String url) {
        if (url == null || !url.startsWith("/spells/")) {
            return null;
        }
        return url.substring("/spells/".length());
    }

    public record ImportResult(int total, int imported, int updated, int failed) {
        @Override
        public String toString() {
            return "total=%d, imported=%d, updated=%d, failed=%d".formatted(total, imported, updated, failed);
        }
    }
}
