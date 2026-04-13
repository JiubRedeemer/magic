package com.jiubredeemer.magic.dto.spellbook;

/**
 * @param status RUNNING — job still executing; DONE — {@code result} set; FAILED — {@code error} set
 */
public record SpellDescriptionSanitizeJobResponse(
        String status,
        SpellDescriptionSanitizeResult result,
        String error
) {
}
