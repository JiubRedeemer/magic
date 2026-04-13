package com.jiubredeemer.magic.service.ttg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strips TTG Club inline markup ({@b ...}, {@glossary ...|...}, {@br}, etc.) from stored text.
 * Ported from item importer logic.
 */
public final class TtgMarkdownSanitizer {

    private static final Pattern BR_TAG_PATTERN = Pattern.compile("\\{@br\\}");
    private static final Pattern SUB_TAG_PATTERN = Pattern.compile("\\{@sub\\s+([^}]*)\\}");
    private static final Pattern SIMPLE_TAG_PATTERN = Pattern.compile("\\{@(b|i|roll)\\s+([^}]*)\\}");
    private static final Pattern LINKED_TAG_PATTERN = Pattern.compile(
            "\\{@(glossary|item|spell|bestiary|magicItem|magicitem|link)\\s+([^}|]*)(?:\\|[^}]*)?\\}");
    private static final Pattern GENERIC_TAG_PATTERN = Pattern.compile("\\{@([^}\\s]+)\\s+([^}]*)\\}");

    private TtgMarkdownSanitizer() {
    }

    public static String sanitize(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }

        String sanitized = raw;
        sanitized = BR_TAG_PATTERN.matcher(sanitized).replaceAll("\n");
        sanitized = SUB_TAG_PATTERN.matcher(sanitized).replaceAll("($1)");
        sanitized = SIMPLE_TAG_PATTERN.matcher(sanitized).replaceAll("$2");
        sanitized = LINKED_TAG_PATTERN.matcher(sanitized).replaceAll("$2");
        sanitized = replaceGenericTags(sanitized);

        sanitized = sanitized.replaceAll("[ \\t]+", " ");
        sanitized = sanitized.replaceAll(" +([,.:;])", "$1");
        sanitized = sanitized.replaceAll("\\n[ \\t]+", "\n");
        sanitized = sanitized.replaceAll("\\n{3,}", "\n\n");
        return sanitized.trim();
    }

    private static String replaceGenericTags(String input) {
        String result = input;
        Matcher matcher = GENERIC_TAG_PATTERN.matcher(result);
        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String body = matcher.group(2) == null ? "" : matcher.group(2).trim();
            String text = body.contains("|") ? body.substring(0, body.indexOf('|')).trim() : body;
            result = result.replace(fullMatch, text);
            matcher = GENERIC_TAG_PATTERN.matcher(result);
        }
        return result.replace("{@", "").replace("}", "");
    }
}
