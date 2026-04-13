package com.jiubredeemer.magic.dto.spellbook;

/**
 * Result of batch TTG markup cleanup on DnD 2024 spell tables.
 */
public record SpellDescriptionSanitizeResult(
        int totalSpell24,
        int changedSpell24,
        int totalSpell24Ai,
        int changedSpell24Ai
) {
}
