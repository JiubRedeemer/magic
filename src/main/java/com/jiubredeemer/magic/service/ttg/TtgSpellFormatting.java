package com.jiubredeemer.magic.service.ttg;

import com.jiubredeemer.magic.dto.ttg.TtgComponents;
import com.jiubredeemer.magic.dto.ttg.TtgSpellClass;
import com.jiubredeemer.magic.dto.ttg.TtgSpellDetail;
import com.jiubredeemer.magic.dto.ttg.v2.TtgV2Affiliation;
import com.jiubredeemer.magic.dto.ttg.v2.TtgV2LocalizedName;
import com.jiubredeemer.magic.dto.ttg.v2.TtgV2SpellDetail;
import com.jiubredeemer.magic.dto.ttg.v2.TtgV2SpellListItem;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.entity.Spell24;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapping from TTG API payloads: v1 (5e14 POST) and v2 (new.ttg.club GET).
 */
public final class TtgSpellFormatting {

    private TtgSpellFormatting() {
    }

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

    public static void mapDetailToSpell(TtgSpellDetail detail, Spell spell) {
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
        spell.setMaterialComponents(formatMaterialComponents(detail.components()));
        spell.setDescription(detail.description());
        spell.setTtgSlug(extractSlug(detail.url()));
    }

    public static void mapV2DetailToSpell24(TtgV2SpellDetail detail, Spell24 spell) {
        spell.setName(v2LocalizedNameToMap(detail.name()));
        spell.setLevel(detail.level() != null ? String.valueOf(detail.level()) : "0");
        spell.setSchool(detail.school() != null ? detail.school() : "");
        spell.setSpellClass(formatClassesV2(detail.affiliation()));
        if (detail.ritual() != null) {
            spell.setRitual(Boolean.TRUE.equals(detail.ritual()));
        }
        if (detail.concentration() != null) {
            spell.setCustomization(Boolean.TRUE.equals(detail.concentration()));
        }
        spell.setUseTime(detail.castingTime());
        spell.setDistance(trimOrNull(detail.range()));
        spell.setDuration(trimOrNull(detail.duration()));
        spell.setComponents(formatComponents(detail.components()));
        spell.setMaterialComponents(formatMaterialComponents(detail.components()));
        spell.setDescription(composeV2Description(detail));
        spell.setTtgSlug(detail.url());
    }

    /**
     * Maps base fields available from v2 search response.
     * Important: concentration/ritual are present there and should be persisted even if detail is partial.
     */
    public static void mapV2ListItemToSpell24(TtgV2SpellListItem item, Spell24 spell) {
        spell.setName(v2LocalizedNameToMap(item.name()));
        spell.setLevel(item.level() != null ? String.valueOf(item.level()) : "0");
        spell.setSchool(item.school() != null ? item.school() : "");
        spell.setRitual(Boolean.TRUE.equals(item.ritual()));
        spell.setCustomization(Boolean.TRUE.equals(item.concentration()));
        spell.setComponents(formatComponents(item.components()));
        spell.setMaterialComponents(formatMaterialComponents(item.components()));
        spell.setTtgSlug(item.slug());
    }

    private static Map<String, String> v2LocalizedNameToMap(TtgV2LocalizedName name) {
        if (name == null) {
            return Map.of();
        }
        var m = new HashMap<String, String>();
        if (name.rus() != null && !name.rus().isBlank()) {
            m.put("rus", name.rus());
        }
        if (name.eng() != null && !name.eng().isBlank()) {
            m.put("eng", name.eng());
        }
        return m.isEmpty() ? Map.of() : Map.copyOf(m);
    }

    private static String composeV2Description(TtgV2SpellDetail detail) {
        StringBuilder sb = new StringBuilder();
        appendV2MixedJson(sb, detail.description());
        if (detail.upper() != null) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            appendV2MixedJson(sb, detail.upper());
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    /**
     * Flattens v2 {@code description}/{@code upper}: Jackson binds arrays to {@link List} of
     * {@link String}, {@link java.util.Map}, etc.
     */
    private static void appendV2MixedJson(StringBuilder sb, Object node) {
        if (node == null) {
            return;
        }
        if (node instanceof List<?> list) {
            for (Object el : list) {
                String block = v2JsonElementToText(el);
                if (block.isEmpty()) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
                sb.append(block);
            }
        } else {
            sb.append(v2JsonElementToText(node));
        }
    }

    private static String v2JsonElementToText(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof String s) {
            return s;
        }
        return o.toString();
    }

    private static String formatClassesV2(TtgV2Affiliation aff) {
        if (aff == null) {
            return "";
        }
        List<TtgSpellClass> list = new ArrayList<>();
        if (aff.classes() != null) {
            for (var c : aff.classes()) {
                if (c != null && c.name() != null) {
                    list.add(new TtgSpellClass(c.name(), c.url(), null));
                }
            }
        }
        if (aff.subclasses() != null) {
            for (var c : aff.subclasses()) {
                if (c != null && c.name() != null) {
                    list.add(new TtgSpellClass(c.name(), c.url(), null));
                }
            }
        }
        if (list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(TtgSpellClass::name)
                .map(name -> name != null ? SPELL_CLASS_NAME_TO_CODE.getOrDefault(
                        stripBracketSourceSuffix(name.trim()),
                        stripBracketSourceSuffix(name.trim())) : name)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private static String stripBracketSourceSuffix(String name) {
        int i = name.lastIndexOf('[');
        if (i > 0) {
            return name.substring(0, i).trim();
        }
        return name.trim();
    }

    private static String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public static String formatClasses(List<TtgSpellClass> classes) {
        if (classes == null || classes.isEmpty()) {
            return "";
        }
        return classes.stream()
                .map(TtgSpellClass::name)
                .map(name -> name != null ? SPELL_CLASS_NAME_TO_CODE.getOrDefault(name.trim(), name) : name)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    public static String formatComponents(TtgComponents components) {
        if (components == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(components.v())) {
            sb.append("V");
        }
        if (Boolean.TRUE.equals(components.s())) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append("S");
        }
        if (hasMaterialComponent(components.m())) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append("M");
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    public static String formatMaterialComponents(TtgComponents components) {
        if (components == null) {
            return null;
        }
        Object m = components.m();
        if (m == null) {
            return null;
        }
        if (m instanceof String s) {
            if (s.isBlank()) {
                return null;
            }
            return s.trim();
        }
        if (m instanceof Boolean b) {
            return b ? "M" : null;
        }
        String asString = m.toString();
        return asString.isBlank() ? null : asString.trim();
    }

    public static boolean hasMaterialComponent(Object m) {
        if (m == null) {
            return false;
        }
        if (m instanceof Boolean b) {
            return b;
        }
        if (m instanceof String s) {
            return !s.isBlank();
        }
        return false;
    }

    public static String extractSlug(String url) {
        if (url == null || !url.startsWith("/spells/")) {
            return null;
        }
        return url.substring("/spells/".length());
    }
}
